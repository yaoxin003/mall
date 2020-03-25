package com.yx.mall.order.controller;

import com.yx.mall.annotations.LoginRequired;
import com.yx.mall.bean.CartItem;
import com.yx.mall.bean.MemberReceiveAddress;
import com.yx.mall.bean.Order;
import com.yx.mall.bean.OrderItem;
import com.yx.mall.constant.MallConstant;
import com.yx.mall.service.CartService;
import com.yx.mall.service.MemberReceiveAddressService;
import com.yx.mall.service.OrderService;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author: yx
 * @date: 2019/12/20/15:42
 */
@Controller
@Log4j
public class OrderController {

    @Reference
    private MemberReceiveAddressService memberReceiveAddressService;

    @Reference
    private CartService cartService;

    @Reference
    private OrderService orderService;

/**
    * @description: 去结算功能
    * @author:  YX
    * @date:    2019/12/21 14:27
    * @param: request
    * @param: modelMap
    * @return: java.lang.String
    * @throws:
    */
    @LoginRequired(loginSuccess=true)
    @RequestMapping("toTrade")
    public String toTrade(HttpServletRequest request,ModelMap modelMap){

        //member信息
        String memberIdStr = (String)request.getAttribute("memberId");
        String nickName = (String)request.getAttribute("nickname");
        log.debug("【memberId=】" + memberIdStr + ",【nickname=】" + nickName);
        if(StringUtils.isNotBlank(memberIdStr)){
            Long memberId = Long.valueOf(memberIdStr);

            //member地址
            List<MemberReceiveAddress> memberReceiveAddressList = memberReceiveAddressService
                    .getMemberReceiveAddressListByMemberId(memberId);
            modelMap.put("userAddressList",memberReceiveAddressList);

            //订单信息
            List<CartItem> cartItems = cartService.cartListByCache(memberId);
            Order order = this.buildOrderFromCartItemList(cartItems);
            List<OrderItem> orderItems = order.getOrderItems();
            BigDecimal orderTotalAmt = order.getTotalAmount();

            //tradeCode
            String tradeCode = orderService.genTradeCode(memberIdStr);

            modelMap.put("orderDetailList",orderItems);
            modelMap.put("totalAmount",orderTotalAmt);
            modelMap.put("tradeCode",tradeCode);

            log.debug("【memberReceiveAddressList=】" + memberReceiveAddressList);
            log.debug("【order=】" + order);
            log.debug("【tradeCode=】" + tradeCode);
        }
        return "trade";
    }

/**
    * @description: 提交订单
    * 验证交易码有效的情况下：
    * 查询用户购物车数据，循环如下：
    * 1.验证库存和价格
    * 2.删除购物车选中数据同时添加订单和订单详情（同一事务）
    * @author:  YX
    * @date:    2019/12/22 8:47
    * @param: request
    * @param: tradeCode
    * @return: java.lang.String
    * @throws:
    */
    @RequestMapping("submitOrder")
    @LoginRequired(loginSuccess = true)
    public ModelAndView submitOrder(String deliveryAddress, String tradeCode, HttpServletRequest request){
        ModelAndView modelAndView = new ModelAndView();
        String returnURL = "tradeFail";
        log.debug("【deliveryAddress=】" + deliveryAddress +"，【tradeCode=】" + tradeCode );

        String memberIdStr = (String)request.getAttribute("memberId");
        String nickName = (String)request.getAttribute("nickname");
        log.debug("【memberIdStr=】" + memberIdStr);

        Long memberId = null;
        if(StringUtils.isNotBlank(memberIdStr)){
            memberId = Long.valueOf(memberIdStr);
            String checkTradeCodeRet = orderService.checkTradeCode(memberIdStr,tradeCode);
            log.debug("【checkTradeCodeRet=】" + checkTradeCodeRet);
            if(MallConstant.RESULT_INFO_SUCCESS.equals(checkTradeCodeRet)){//验证交易码：有效
                List<CartItem> cartItemCaches = cartService.cartListByCache(memberId);
                //删除购物车并添加订单和订单详细信息
                Order order = orderService.delCartItemListAndAddOrder(memberId,nickName,deliveryAddress,cartItemCaches);
                if(order != null){
                    returnURL = "redirect:" + MallConstant.MALL_PAYMENT_URI;
                    modelAndView.addObject("orderSn",order.getOrderSn());
                }
            }
        }
        modelAndView.setViewName(returnURL);
        return modelAndView;
    }

/**
    * @description: 封装订单总价和订单明细
    * @author:  YX
    * @date:    2019/12/21 10:09
    * @param: cartItems
    * @return: com.yx.mall.bean.Order
    */
    private Order buildOrderFromCartItemList(List<CartItem> cartItems) {
        BigDecimal orderTotalAmount = new BigDecimal("0");
        Order order = new Order();
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            if(MallConstant.IS_CHECKED.equals(cartItem.getIsChecked())){
            OrderItem orderItem = new OrderItem();
            orderItem.setProductSkuId(cartItem.getProductSkuId());
            orderItem.setProductName(cartItem.getProductName());
            orderItem.setProductId(cartItem.getProductId());
            orderItem.setProductCategoryId(cartItem.getProductCategoryId());
            orderItem.setProductPic(cartItem.getProductPic());
            orderItem.setProductPrice(cartItem.getPrice());
            orderItem.setProductQuantity(cartItem.getQuantity());
            orderItems.add(orderItem);
            cartItem.setTotalPrice(new BigDecimal(String.valueOf(cartItem.getQuantity())).
                    multiply(cartItem.getPrice()));
            orderTotalAmount = orderTotalAmount.add(cartItem.getTotalPrice());
            }
        }
        order.setOrderItems(orderItems);
        order.setTotalAmount(orderTotalAmount);
        return order;
    }

}
