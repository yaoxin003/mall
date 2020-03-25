package com.yx.mall.payment.controller;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.yx.mall.annotations.LoginRequired;
import com.yx.mall.bean.Order;
import com.yx.mall.bean.OrderItem;
import com.yx.mall.bean.PaymentInfo;
import com.yx.mall.constant.MallConstant;
import com.yx.mall.mq.ActiveMQUtil;
import com.yx.mall.payment.config.AlipayConfig;
import com.yx.mall.service.OrderService;
import com.yx.mall.service.PaymentService;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:支付服务
 * @author: yx
 * @date: 2019/12/24/14:15
 */
@Log4j
@Controller
public class PaymentController {

    @Reference
    private OrderService orderService;

    @Autowired
    AlipayClient alipayClient;

    @Reference
    private PaymentService paymentService;

    @RequestMapping("index")
    @LoginRequired(loginSuccess = true)
    public String index(HttpServletRequest request, ModelMap modelMap){

        String memberId = (String)request.getAttribute("memberId");
        String nickName = (String)request.getAttribute("nickname");
        String orderSn = (String)request.getParameter("orderSn");

        log.debug("【memberId=】" + memberId);
        log.debug("【nickName=】" + nickName);
        log.debug("【orderSn=】" + orderSn);

        Order dbOrder = null;
        BigDecimal totalAmount = BigDecimal.ZERO;
        if(StringUtils.isNotBlank(orderSn)){
            Order paramOrder = new Order();
            paramOrder.setOrderSn(orderSn);
            dbOrder = orderService.getOneOrder(paramOrder);
            totalAmount = dbOrder.getTotalAmount();
        }
        log.debug("【dbOrder=】" + dbOrder);
        log.debug("【totalAmount=】" + totalAmount);

        modelMap.put("orderId",orderSn);
        modelMap.put("nickName",nickName);
        modelMap.put("totalAmount",totalAmount);
        return "index";
    }

    @RequestMapping("alipay/submit")
    @LoginRequired(loginSuccess = true)
    @ResponseBody
    public String alipay(String orderId){
        String form = null;
        if(StringUtils.isNotBlank(orderId)){
            String orderSn = orderId;
            if(StringUtils.isNotBlank(orderSn)) {
                try {
                    Order order = this.getOrderAndOrderItemsByOrderSn(orderSn);
                    AlipayTradePagePayRequest alipayRequest = this.buildAlipayRequest(order);
                    AlipayTradePagePayResponse alipayTradePagePayResponse = alipayClient.pageExecute(alipayRequest);
                    form = alipayTradePagePayResponse.getBody();
                    int addCount = this.addPaymentByOrder(order,this.getSubjectByOrder(order));
                    //调用延迟队列
                    paymentService.sendPaymentStatusCheckDelayQueue(orderSn,MallConstant.MQ_DELAY_SEND_COUNT);
                } catch (AlipayApiException e) {
                    e.printStackTrace();
                }
            }
        }
        log.debug("【orderId=】" + orderId);
        log.debug("【form=】" + form);
        return form;
    }

/**
    * @description: 浏览器同步接收支付宝成功通知
    * @author:  YX
    * @date:    2019/12/27 13:40
    * @param: request
    * @param: modelMap
    * @return: java.lang.String
 * http://payment.mall.com:8087/alipay/callback/return?out_trade_no=mall201912281332233641577511143394
    */
    @RequestMapping("alipay/callback/return")
    @LoginRequired(loginSuccess=true)
    public String alipayCallbackReturn(HttpServletRequest request,ModelMap modelMap){
        PaymentInfo payment = this.buildSinglePayment(request);
        if(payment != null){
            paymentService.updatePaymentAndSendPaymentSuccessMQ(payment);
        }
        return "finish";
    }

    private PaymentInfo buildSinglePayment(HttpServletRequest request) {
        PaymentInfo payment = null;
        String sign = request.getParameter("sign");
        String tradeNo = request.getParameter("trade_no");
        String orderSn = request.getParameter("out_trade_no");
        String tradeStatus = request.getParameter("trade_status");
        String totalAmount = request.getParameter("total_amount");
        String subject = request.getParameter("subject");
        String callbackContent = request.getQueryString();

        if(StringUtils.isNotBlank(callbackContent)){
            payment = new PaymentInfo();
            payment.setOrderSn(orderSn);
            payment.setPaymentStatus(MallConstant.DB_MALL_PAYMENT_STATUS_PAY_SUCCESS);
            payment.setAlipayTradeNo(tradeNo);
            payment.setCallbackContent(callbackContent);
            payment.setCallbackTime(new Date());
        }
        return payment;
    }

    private int addPaymentByOrder(Order order,String subject){
        PaymentInfo payment = this.buildPaymentByOrder(order,subject);
        log.debug("【payment=】" + payment);
        int count = paymentService.addPayment(payment);
        log.debug("【count=】" + count);
        return count;
    }

    private PaymentInfo buildPaymentByOrder(Order order,String subject) {
        PaymentInfo payment = new PaymentInfo();
        payment.setOrderId(String.valueOf(order.getId()));
        payment.setOrderSn(order.getOrderSn());
        payment.setTotalAmount(order.getTotalAmount());
        payment.setCreateTime(new Date());
        payment.setPaymentStatus(MallConstant.DB_MALL_PAYMENT_STATUS_NO_PAY);
        payment.setSubject(subject);
        return payment;
    }

    private Order getOrderAndOrderItemsByOrderSn(String orderSn) {
        Order retOrder = null;
        Order paramOrder = new Order();
        paramOrder.setOrderSn(orderSn);
        List<Order> orderAndItems = orderService.getOrderAndItems(orderSn);
        if(orderAndItems != null){
            retOrder = orderAndItems.get(0);
        }
        return retOrder;
    }


    private AlipayTradePagePayRequest buildAlipayRequest(Order paramOrder) {
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(AlipayConfig.return_payment_url);
        alipayRequest.setNotifyUrl(AlipayConfig.notify_payment_url);
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("out_trade_no",paramOrder.getOrderSn());
        map.put("product_code","FAST_INSTANT_TRADE_PAY");
        map.put("total_amount",0.01);//dbOrder.getTotalAmount();
        map.put("subject",this.getSubjectByOrder(paramOrder));
        log.debug("【map=】" + map);
        alipayRequest.setBizContent(JSON.toJSONString(map));
        log.debug("【alipayRequest=】" + alipayRequest);
        return alipayRequest;
    }

    private String getSubjectByOrder(Order paramOrder) {
        String subject = "";
        List<OrderItem> orderItems = null;
        if(paramOrder != null && (orderItems=paramOrder.getOrderItems()) != null){
            for (OrderItem orderItem : orderItems) {
                subject += orderItem.getProductName() + ", ";
            }
        }
        return subject;
    }

}
