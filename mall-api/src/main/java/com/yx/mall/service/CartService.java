package com.yx.mall.service;

import com.yx.mall.bean.CartItem;

import java.util.List;

public interface CartService {

    public CartItem getCart(Long memberId, Long skuId);

    public void addCart(CartItem cartItem);

    public void updateCart(CartItem cartItemDB);

    public void flushCartCache(Long memberId );

    public List<CartItem> getCartItemList(Long memberId);

    public List<CartItem> cartListByCache(Long memberId);

    public void checkCart(CartItem cartItem);

    public CartItem getOneCart(CartItem paramCartItem);

    public int deleteCheckedCartItemsInCache(Long memberId,List<CartItem> cartItemCaches);

    public int deleteCheckedCartItems(List<CartItem> cartItemCaches);
}
