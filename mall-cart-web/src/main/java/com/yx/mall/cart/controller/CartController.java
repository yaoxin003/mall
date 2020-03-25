package com.yx.mall.cart.controller;

import com.alibaba.fastjson.JSON;
import com.yx.mall.annotations.LoginRequired;
import com.yx.mall.bean.CartItem;
import com.yx.mall.bean.SkuInfo;
import com.yx.mall.constant.MallConstant;
import com.yx.mall.service.CartService;
import com.yx.mall.service.SkuService;
import com.yx.mall.util.CookieUtil;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Log4j
@Controller
public class CartController {

    @Reference
    private SkuService skuService;

    @Reference
    private CartService cartService;

    @LoginRequired(loginSuccess = false)
    @RequestMapping("addToCart")
    public String addToCart(Integer num, Long skuId, HttpServletRequest request, HttpServletResponse response){
        log.debug("【num=】"+num + ",【skuId=】" + skuId);
        SkuInfo skuInfo = skuService.getSkuInfoByIdFromDBAndCache(skuId);
        CartItem currentCartItem = this.buildCartItem(num,skuId,skuInfo);

        //1.判断用户是否登录
        Long memberId = null;
        memberId = this.getMemberIdFromRequestParam(request);
        if(memberId != null){
            //1.1若登录则进行DB和Cache操作
            log.debug("【1.1若登录则进行DB和Cache操作】");
            //查询DB验证数据是否存在
            log.debug("【查询DB验证数据是否存在】memberId="+memberId+",skuId="+skuId);
            CartItem cartItemDB = cartService.getCart(memberId,skuId);
            if(cartItemDB == null){
                log.debug("【不存在，插入数据】memberId="+memberId+",skuId="+skuId);
                //不存在，插入数据
                currentCartItem.setMemberId(memberId);//memberId
                cartService.addCart(currentCartItem);
            }else{
                log.debug("【存在，更新数据】memberId="+memberId+",skuId="+skuId);
                //存在，更新数据
                cartItemDB.setQuantity(num);
                cartService.updateCart(cartItemDB);
            }
            //刷新Cache数据
            log.debug("【刷新Cache数据】memberId="+memberId);
            cartService.flushCartCache(memberId);
        }else{
            log.debug("【1.2若未登录则进行Cookie操作】");
            //1.2若未登录则进行Cookie操作
            String cartListCookieStr = CookieUtil.getCookieValue(request, MallConstant.COOKIE_CART_LIST_COOKIE,true);
            List<CartItem> cookieCartItmes = null;
            if(StringUtils.isBlank(cartListCookieStr)){
                log.debug("【cookie中无购物车数据】");
                //加入cookie中
                cookieCartItmes = new ArrayList<CartItem>();
                cookieCartItmes.add(currentCartItem);
            }else{
                log.debug("【cookie中有购物车数据cartListCookieStr=】"+cartListCookieStr);
                cookieCartItmes = JSON.parseArray(cartListCookieStr, CartItem.class);
                boolean exists = ifCartExists(cookieCartItmes,skuId);
                if(exists){
                    log.debug("【cookie中有购物车数据，且有skuId=】" + skuId);
                    //更新购物车数据
                    for (CartItem cookieCartItme : cookieCartItmes) {
                        if(cookieCartItme.getProductSkuId().equals(skuId)){
                            cookieCartItme.setQuantity(cookieCartItme.getQuantity()+currentCartItem.getQuantity());
                        }
                    }
                }else{
                    log.debug("【cookie中有购物车数据，但无skuId=】" + skuId);
                    //添加购物车数据
                    cookieCartItmes.add(currentCartItem);
                }
            }
            String resCookieStr = JSON.toJSONString(cookieCartItmes);
            log.debug("【resCookieStr=】" + resCookieStr);
            CookieUtil.setCookie(request,response,MallConstant.COOKIE_CART_LIST_COOKIE,
                    resCookieStr,60*60*24*3,true);
        }
        return "redirect:/success.html";
    }

    @LoginRequired(loginSuccess = false)
    @RequestMapping("cartList")
    public String cartList(HttpServletRequest request,HttpServletResponse response,ModelMap modelMap){
        Long memberId = null;
        memberId = this.getMemberIdFromRequestParam(request);

        List<CartItem> cartList = null;
        if(memberId != null){
            log.debug("已登录，从缓存中查询");
            //已登录，从缓存中查询
            cartList = cartService.cartListByCache(memberId);
        }else {
            //未登录，从cookie中查询
            log.debug("未登录，从cookie中查询");
            String cookieName = MallConstant.COOKIE_CART_LIST_COOKIE;
            String cartStr = CookieUtil.getCookieValue(request, cookieName, true);
            if(StringUtils.isNotBlank(cartStr)){
                cartList = JSON.parseArray(cartStr, CartItem.class);
            }
        }
        if(cartList != null && !cartList.isEmpty()){
            modelMap.put("cartList",cartList);
            BigDecimal totalAmount = this.getTotalAmount(cartList);
            modelMap.put("totalAmount",totalAmount);
        }
        return "cartList";
    }

    @LoginRequired(loginSuccess = false)
    @RequestMapping("checkCart")
    public String checkCart(HttpServletRequest request,String isChecked,Long skuId,ModelMap modelMap){
        Long memberId = null;
        memberId = this.getMemberIdFromRequestParam(request);

        CartItem cartItem = new CartItem();
        cartItem.setMemberId(memberId);
        cartItem.setProductSkuId(skuId);
        cartItem.setIsChecked(isChecked);
        cartService.checkCart(cartItem);

        List<CartItem> cartListCache = cartService.cartListByCache(memberId);
        log.debug("【cartListCache=】" + cartListCache);
        if(cartListCache != null && !cartListCache.isEmpty()) {
            modelMap.put("cartList", cartListCache);
            BigDecimal totalAmount = this.getTotalAmount(cartListCache);
            modelMap.put("totalAmount", totalAmount);
        }
        return "cartListInner";
    }

    private Long getMemberIdFromRequestParam(HttpServletRequest request) {
        Long memberId = null;
        String memberIdStr = (String)request.getAttribute("memberId");
        if(StringUtils.isNotBlank(memberIdStr)){
            memberId = Long.parseLong(memberIdStr);
        }
        log.debug("【request.get memberId=】" + memberId);
        return memberId;
    }

    private BigDecimal getTotalAmount(List<CartItem> cartList) {
        BigDecimal totalAmout = new BigDecimal("0");
        for (CartItem cartItem : cartList) {
            BigDecimal quantity = new BigDecimal(String.valueOf(cartItem.getQuantity()));
            cartItem.setTotalPrice(cartItem.getPrice().multiply(quantity));
            if(MallConstant.IS_CHECKED.equals(cartItem.getIsChecked())){
                totalAmout = totalAmout.add(cartItem.getTotalPrice());
            }
        }
        return totalAmout;
    }

    private CartItem buildCartItem(Integer num, Long skuId, SkuInfo skuInfo) {
        CartItem cartItem = new CartItem();
        cartItem.setQuantity(num);
        cartItem.setProductSkuId(skuId);
        cartItem.setCreateDate(new Date());
        cartItem.setPrice(BigDecimal.valueOf(skuInfo.getPrice()));
        cartItem.setProductCategoryId(skuInfo.getCatalog3Id());
        cartItem.setProductId(skuInfo.getSpuId());
        cartItem.setProductName(skuInfo.getSkuName());
        cartItem.setProductPic(skuInfo.getSkuDefaultImg());
        return cartItem;
    }

    private boolean ifCartExists(List<CartItem> cookieCartItmes, Long skuId) {
        boolean exists = false;
        if(cookieCartItmes != null && !cookieCartItmes.isEmpty()){
            for (CartItem cookieCartItme : cookieCartItmes) {
                if(cookieCartItme.getProductSkuId().equals(skuId)){
                    exists = true;
                }
            }
        }
        return exists;
    }



}
