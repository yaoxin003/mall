package com.yx.mall.bean;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Table(name="payment_info")
public class PaymentInfo implements Serializable{

    @Id
    private Long id;

    private String orderSn;

    private String orderId;

    private String alipayTradeNo;

    private BigDecimal totalAmount;

    private String subject;

    private String paymentStatus;

    private Date createTime;

    private Date confirmTime;

    private String callbackContent;

    private Date callbackTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderSn() {
        return orderSn;
    }

    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn == null ? null : orderSn.trim();
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId == null ? null : orderId.trim();
    }

    public String getAlipayTradeNo() {
        return alipayTradeNo;
    }

    public void setAlipayTradeNo(String alipayTradeNo) {
        this.alipayTradeNo = alipayTradeNo == null ? null : alipayTradeNo.trim();
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject == null ? null : subject.trim();
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus == null ? null : paymentStatus.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getConfirmTime() {
        return confirmTime;
    }

    public void setConfirmTime(Date confirmTime) {
        this.confirmTime = confirmTime;
    }

    public String getCallbackContent() {
        return callbackContent;
    }

    public void setCallbackContent(String callbackContent) {
        this.callbackContent = callbackContent == null ? null : callbackContent.trim();
    }

    public Date getCallbackTime() {
        return callbackTime;
    }

    public void setCallbackTime(Date callbackTime) {
        this.callbackTime = callbackTime;
    }

    @Override
    public String toString() {
        return "PaymentInfo{" +
                "id=" + id +
                ", orderSn='" + orderSn + '\'' +
                ", orderId='" + orderId + '\'' +
                ", alipayTradeNo='" + alipayTradeNo + '\'' +
                ", totalAmount=" + totalAmount +
                ", subject='" + subject + '\'' +
                ", paymentStatus='" + paymentStatus + '\'' +
                ", createTime=" + createTime +
                ", confirmTime=" + confirmTime +
                ", callbackContent='" + callbackContent + '\'' +
                ", callbackTime=" + callbackTime +
                '}';
    }
}