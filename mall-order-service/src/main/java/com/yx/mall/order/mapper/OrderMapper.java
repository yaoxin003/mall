package com.yx.mall.order.mapper;

import com.yx.mall.bean.Order;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @description:
 * @author: yx
 * @date: 2019/12/22/14:55
 */
public interface OrderMapper extends Mapper<Order>{
    public List<Order> selectOrderAndItems(String orderSn);
}
