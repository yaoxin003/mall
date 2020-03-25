package com.yx.mall.service;

import com.yx.mall.bean.CartItem;
import com.yx.mall.bean.Order;
import com.yx.mall.vo.OmsOrder;

import java.util.List;

/**
 * @description:
 * @author: yx
 * @date: 2019/12/21/13:17
 */
public interface OrderService {

    public String genTradeCode(String memberId);

    public String checkTradeCode(String memberId,String tradeCode);

    public Order delCartItemListAndAddOrder(Long memberId,String nickName,String deliveryAddressId,List<CartItem> cartItemCaches);

    public Order getOneOrder(Order paramOrder);

    public List<Order> getOrderAndItems(String orderSn);

    public void updateOrderAndSendOrderPayQueue(String orderSn);

    public List<OmsOrder> buildOrderAndOrderItemVos(List<Order> orders);
}
