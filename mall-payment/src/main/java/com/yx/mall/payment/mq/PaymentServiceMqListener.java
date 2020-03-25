package com.yx.mall.payment.mq;

import com.yx.mall.bean.PaymentInfo;
import com.yx.mall.constant.MallConstant;
import com.yx.mall.service.PaymentService;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import java.util.Date;
import java.util.Map;

/**
 * @description:
 * @author: yx
 * @date: 2019/12/29/16:20
 */
@Log4j
@Component
public class PaymentServiceMqListener {

    @Autowired
    private PaymentService paymentService;

    @JmsListener(destination = MallConstant.MQ_PAYMENT_CHECK_QUEUE,containerFactory = "jmsQueueListener")
    public void consumePaymentStatusCheckResult(MapMessage mapMessage) throws JMSException{
        String orderSn = mapMessage.getString("orderSn");
        String sendCount = mapMessage.getString("sendCount");
        int count = 0;
        log.debug("【orderSn=】" + orderSn + ",【sendCount=】" + sendCount);
        if(StringUtils.isNotBlank(orderSn) && StringUtils.isNotBlank(sendCount)){
            //1.调用支付宝查询接口：统一收单线下交易查询
            Map<String,Object> resultMap = paymentService.queryAlipayPayment(orderSn);
            if(resultMap != null && !resultMap.isEmpty()){
                log.debug("【调用支付宝查询接口，支付成功】");
                //1.1支付成功：调用更新PaymentService.updatePayment
                String tradeStatus = (String)resultMap.get("tradeStatus");
                if(StringUtils.isNotBlank(tradeStatus) && tradeStatus.equals(
                        MallConstant.ALIPAY_TRADE_QUERY_RESPONSE_TRADE_SUCCESS)){
                    PaymentInfo payment = new PaymentInfo();
                    payment.setOrderSn((String)resultMap.get("orderSn"));
                    payment.setPaymentStatus(MallConstant.DB_MALL_PAYMENT_STATUS_PAY_SUCCESS);
                    payment.setOrderSn(orderSn);
                    payment.setAlipayTradeNo((String)resultMap.get("tradeNo"));
                    payment.setCallbackTime(new Date());
                    paymentService.updatePaymentAndSendPaymentSuccessMQ(payment);
                }
            }else{
                log.debug("【调用支付宝，支付未成功】");
                count = Integer.parseInt(sendCount);
                log.debug("【count=】" + count);
                //1.2非成功，且调用次数小于5次，则调用发送延迟MQ
                if(count >0){
                    --count;
                    paymentService.sendPaymentStatusCheckDelayQueue(orderSn,count);
                }
            }
        }
    }
}
