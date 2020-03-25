package com.yx.mall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.yx.mall.bean.CartItem;
import com.yx.mall.cart.mapper.CartItemMapper;
import com.yx.mall.constant.MallConstant;
import com.yx.mall.service.CartService;
import com.yx.mall.util.RedisUtil;
import lombok.extern.log4j.Log4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Condition;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Service
@Log4j
public class CartServiceImpl implements CartService {

    @Autowired
    private CartItemMapper cartItemMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public CartItem getCart(Long memberId, Long skuId) {
        CartItem cartItem = new CartItem();
        cartItem.setMemberId(memberId);
        cartItem.setProductSkuId(skuId);
        CartItem resCartItem = cartItemMapper.selectOne(cartItem);
        log.debug("【resCartItem=】"+ resCartItem);
        return resCartItem;
    }

    @Override
    public void addCart(CartItem cartItem) {
        cartItemMapper.insertSelective(cartItem);
    }

    @Override
    public void updateCart(CartItem cartItemDB) {
        Example example = new Example(CartItem.class);
        example.createCriteria().andEqualTo("id",cartItemDB.getId());
        cartItemMapper.updateByExampleSelective(cartItemDB,example);
    }
/**
    * @description: 刷新购物车缓存
    * redis数据结构： zset。
    * key为user:1:card
    * score为skuId
    * member为CartItem的json
    * redis语法：添加：zdd key score1 member1，
    * 数量：zcard key
    * 倒序获得数据集合：zrevrange key 0 -1
    * 倒序获得数据集合和分数：zrange key 0 -1 withscores
    * 删除指定分数member：zremrangebyscore key score1 score1
    * 删除数据：del key
    * @author:  YX
    * @date:    2019/12/22 12:03
    * @param: memberId
    * @return: void
    */
    @Override
    public void flushCartCache(Long memberId) {
        Jedis cacheJedis = null;
        try{
            List<CartItem> cartItems = this.getCartItemList(memberId);
            log.debug("【cartItems=】"+cartItems);
            String cacheKey = MallConstant.CACHE_USER_MEMBERID_CARD_PRE + memberId
                    + MallConstant.CACHE_USER_MEMBERID_CARD_SUF;
            cacheJedis = redisUtil.getCacheJedis();
            log.debug("【删除Cache】"+ cacheKey);
            cacheJedis.del(cacheKey);
            log.debug("【插入Cache】"+ cacheKey);
            for (CartItem item : cartItems) {
                cacheJedis.zadd(cacheKey,item.getProductSkuId(),
                        JSON.toJSONString(item));
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(cacheJedis != null){
                cacheJedis.close();
            }
        }

    }

    public List<CartItem> getCartItemList(Long memberId) {
        Condition condition = new Condition(CartItem.class);
        condition.setOrderByClause("id desc");
        Example.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("memberId",memberId);
        List<CartItem> cartItems = cartItemMapper.selectByExample(condition);
        log.debug("【cartItems】=" + cartItems);
        return cartItems;
    }

    public List<CartItem> cartListByCache(Long memberId){
        List<CartItem> cartItemCaches = new ArrayList<>();
        Jedis cacheJedis = redisUtil.getCacheJedis();
        String cacheKey = MallConstant.CACHE_USER_MEMBERID_CARD_PRE + memberId
                + MallConstant.CACHE_USER_MEMBERID_CARD_SUF;
        Set<String> itemCacheSet = cacheJedis.zrevrange(cacheKey,0,-1);//倒序排列
        for (String cartItemStr : itemCacheSet) {
            CartItem cartItem = JSON.parseObject(cartItemStr,CartItem.class);
            cartItemCaches.add(cartItem);
        }
        log.debug("【cartItemCaches.size=】" + cartItemCaches.size() + ",【cartItemCaches=】" + cartItemCaches);
        return cartItemCaches;
    }

    @Override
    public void checkCart(CartItem cartItem) {
        Long memberId = cartItem.getMemberId();
        Example example = new Example(CartItem.class);
        example.createCriteria().andEqualTo("memberId",memberId)
                    .andEqualTo("productSkuId",cartItem.getProductSkuId());
        int updateCount = cartItemMapper.updateByExampleSelective(cartItem,example);
        log.debug("【updateCount=】" + updateCount);
        this.flushCartCache(memberId);
    }

    @Override
    public CartItem getOneCart(CartItem paramCartItem) {
        CartItem retCartItem = null;
        List<CartItem> dbCartItems = cartItemMapper.select(paramCartItem);
        if(dbCartItems != null && dbCartItems.size() > 0){
            retCartItem = dbCartItems.get(0);
        }
        log.debug("【retCartItem=】" + retCartItem);
        return retCartItem;
    }

/**
    * @description: 删除缓存中选中的购物车数据
    * 删除指定分数member：zremrangebyscore key score1 score1
    * @author:  YX
    * @date:    2019/12/22 14:35
    * @param: cartItemCaches
    * @return: int
    */
    @Override
    public int deleteCheckedCartItemsInCache(Long memberId,List<CartItem> cartItemCaches) {
        String cacheKey = MallConstant.CACHE_USER_MEMBERID_CARD_PRE + memberId
                + MallConstant.CACHE_USER_MEMBERID_CARD_SUF;
        Jedis cacheJedis = redisUtil.getCacheJedis();
        int delCount = 0;
        for (CartItem cartItemCach : cartItemCaches) {
            if(MallConstant.IS_CHECKED.equals(cartItemCach.getIsChecked())){
                Long skuId = cartItemCach.getProductSkuId();
                cacheJedis.zremrangeByScore(cacheKey,skuId,skuId);
                ++delCount;
                log.debug("【cacheKey=】"+ cacheKey + "，【skuId=】"+skuId + "，【delCount=】" + delCount);
            }
        }
        return delCount;
    }

    @Override
    public int deleteCheckedCartItems(List<CartItem> cartItemCaches) {
       //选中id
        List<Long> ids = new ArrayList<>();
        for (CartItem cartItemCach : cartItemCaches) {
            if(MallConstant.IS_CHECKED.equals(cartItemCach.getIsChecked())){
                ids.add(cartItemCach.getId());
            }
        }
        log.debug("【ids=】" + ids);
        Example example = new Example(CartItem.class);
        example.createCriteria().andIn("id",ids);
        int delCount = cartItemMapper.deleteByExample(example);
        log.debug("【delCount=】" + delCount);
        return delCount;
    }
}
