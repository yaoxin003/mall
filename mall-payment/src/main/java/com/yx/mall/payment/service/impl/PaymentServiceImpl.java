package com.yx.mall.payment.service.impl;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.yx.mall.bean.PaymentInfo;
import com.yx.mall.constant.MallConstant;
import com.yx.mall.mq.ActiveMQUtil;
import com.yx.mall.payment.mapper.PaymentMapper;
import com.yx.mall.service.PaymentService;
import lombok.extern.log4j.Log4j;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import javax.jms.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @author: yx
 * @date: 2019/12/25/14:15
 */
@Log4j
@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentMapper paymentMapper;

    @Autowired
    private ActiveMQUtil activeMQUtil;

    @Autowired
    private AlipayClient alipayClient;

    @Override
    public int addPayment(PaymentInfo payment) {
        int insertCount = paymentMapper.insertSelective(payment);
        log.debug("【insertCount=】" + insertCount);
        return insertCount;
    }

/**
    * @description: 加入幂等性验证，同一事务：更新支付状态并加入消息队列（持久化MQ）
    * @author:  YX
    * @date:    2019/12/27 13:39
    * @param: payment
    * @return: int
    */
    @Override
    @Transactional
    public int updatePaymentAndSendPaymentSuccessMQ(PaymentInfo payment){
        log.debug("【payment=】" + payment);
        int updateCount = 0;
        String orderSn = payment.getOrderSn();
        //幂等性检查
        if(StringUtils.isNotBlank(orderSn)){
            log.debug("【准备幂等性检查】");
            PaymentInfo onePaymentByOrderSn = this.getOnePaymentByOrderSn(orderSn);
            log.debug("【onePaymentByOrderSn=】" + onePaymentByOrderSn);
            if(onePaymentByOrderSn != null && MallConstant.DB_MALL_PAYMENT_STATUS_PAY_SUCCESS.
                    equals(onePaymentByOrderSn.getPaymentStatus())){
               log.debug("【幂等性检查，已经更新payment和order】");
            }else{
                updateCount = this.updatePaymentByOrderSn(payment);
                this.sendPaymentSuccessMQ(payment);
            }
        }
        return updateCount;
    }

    /**
        * @description: 发送支付状态检查延迟队列
        * @author:  YX
        * @date:    2019/12/29 16:01
        * @param: orderSn
        * @return: void
        * @throws:
        */
    @Override
    public void sendPaymentStatusCheckDelayQueue(String orderSn,int sendCount) {
        log.debug("【orderSn=】" + orderSn + "，【sendCount=】" +sendCount);
        //发送延迟MQ
        try{
            MapMessage mapMessage = new ActiveMQMapMessage();
            mapMessage.setString("orderSn",orderSn);
            mapMessage.setInt("sendCount",sendCount);
            activeMQUtil.sendDelayMQ(MallConstant.MQ_PAYMENT_CHECK_QUEUE,mapMessage,MallConstant.MQ_DELAY_TIME);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

/**
    * @description: 调用支付宝查询接口：“统一收单线下交易查询”
    * @author:  YX
    * @date:    2019/12/29 17:30
    * @param: orderSn
    * @return: java.util.Map<java.lang.String,java.lang.Object>
    * @throws: 
    */
    @Override
    public Map<String, Object> queryAlipayPayment(String orderSn) {
        log.debug("调用支付宝查询接口：统一收单线下交易查询");
        Map<String,Object> resultMap = new HashMap<>();
        log.debug("【orderSn=】" + orderSn);
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        Map<String,Object> map = new HashMap<>();
        map.put("orderSn",orderSn);
        request.setBizContent(JSON.toJSONString(map));
        AlipayTradeQueryResponse response = null;
         try {
            response = alipayClient.execute(request);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        if(response.isSuccess()){
            response = new AlipayTradeQueryResponse();
            response.setTradeStatus(MallConstant.ALIPAY_TRADE_QUERY_RESPONSE_TRADE_SUCCESS);
            response.setOutTradeNo(orderSn);

            log.debug("【支付成功】");
            resultMap.put("orderSn",response.getOutTradeNo());
            resultMap.put("tradeNo",response.getTradeNo());
            resultMap.put("tradeStatus",response.getTradeStatus());
        }else{
            log.debug("【未支付成功】");
        }
        return resultMap;
    }



    private PaymentInfo getOnePaymentByOrderSn(String orderSn) {
        PaymentInfo payment = new PaymentInfo();
        payment.setOrderSn(orderSn);
        PaymentInfo paymentDB = paymentMapper.selectOne(payment);
        log.debug("【paymentDB=】" + paymentDB);
        return paymentDB;
    }

    private boolean sendPaymentSuccessMQ(PaymentInfo payment){
        log.debug("【payment=】" + payment);
        boolean ret = false;
        try {
            MapMessage mapMessage = new ActiveMQMapMessage();
            mapMessage.setString("orderSn",payment.getOrderSn());
            activeMQUtil.sendTransactedMQ(MallConstant.MQ_PAYMENT_SUCCESS_QUEUE,mapMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public int updatePaymentByOrderSn(PaymentInfo payment) {
        log.debug("【payment=】" + payment);
        //更新支付状态
        Example example = new Example(PaymentInfo.class);
        example.createCriteria().andEqualTo("orderSn",payment.getOrderSn());
        payment.setPaymentStatus(MallConstant.DB_MALL_PAYMENT_STATUS_PAY_SUCCESS);
        int updateCount = paymentMapper.updateByExampleSelective(payment, example);
        log.debug("【updateCount=】" + updateCount);
        return updateCount;
    }

}
