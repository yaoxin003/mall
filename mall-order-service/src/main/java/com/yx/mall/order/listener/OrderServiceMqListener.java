package com.yx.mall.order.listener;

import com.yx.mall.constant.MallConstant;
import com.yx.mall.service.OrderService;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import javax.jms.JMSException;
import javax.jms.MapMessage;

/**
 * @description:监听MQ消息，
 * @author: yx
 * @date: 2019/12/27/16:13
 */
@Log4j
@Component
public class OrderServiceMqListener {

    @Autowired
    private OrderService orderService;

    @JmsListener(destination = MallConstant.MQ_PAYMENT_SUCCESS_QUEUE,containerFactory = "jmsQueueListener")
    public void comsumePaymentResult(MapMessage mapMessage) throws JMSException {
        String orderSn = mapMessage.getString("orderSn");
        log.debug("【orderSn=】" + orderSn);
        if(StringUtils.isNotBlank(orderSn)){
            orderService.updateOrderAndSendOrderPayQueue(orderSn);
        }
    }

}
