package com.yx.mall.service;

import com.yx.mall.bean.Order;
import com.yx.mall.bean.PaymentInfo;

import java.util.Map;

/**
 * @description:
 * @author: yx
 * @date: 2019/12/25/14:14
 */
public interface PaymentService {

    public int addPayment(PaymentInfo payment);

    public int updatePaymentByOrderSn(PaymentInfo payment);

    public int updatePaymentAndSendPaymentSuccessMQ(PaymentInfo payment);

    public void sendPaymentStatusCheckDelayQueue(String orderSn,int sendCount);

    public Map<String,Object> queryAlipayPayment(String orderSn);

}
