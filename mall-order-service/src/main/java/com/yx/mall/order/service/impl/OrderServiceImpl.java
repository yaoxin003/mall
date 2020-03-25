package com.yx.mall.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.yx.mall.bean.CartItem;
import com.yx.mall.bean.MemberReceiveAddress;
import com.yx.mall.bean.Order;
import com.yx.mall.bean.OrderItem;
import com.yx.mall.constant.MallConstant;
import com.yx.mall.mq.ActiveMQUtil;
import com.yx.mall.order.mapper.OrderItemMapper;
import com.yx.mall.order.mapper.OrderMapper;
import com.yx.mall.service.CartService;
import com.yx.mall.service.MemberReceiveAddressService;
import com.yx.mall.service.OrderService;
import com.yx.mall.util.RedisUtil;
import com.yx.mall.vo.OmsOrder;
import com.yx.mall.vo.OmsOrderItem;
import lombok.extern.log4j.Log4j;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.persistence.Transient;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @description:
 * @author: yx
 * @date: 2019/12/21/13:14
 */
@Log4j
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private RedisUtil redisUtil;

    @Reference
    private CartService cartService;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Reference
    private MemberReceiveAddressService memberReceiveAddressService;

    @Autowired
    private ActiveMQUtil activeMQUtil;


    @Override
    public String genTradeCode(String memberId) {
        Jedis cacheJedis = null;
        String tradeCode = null;
        try{
            cacheJedis = redisUtil.getCacheJedis();
            tradeCode = UUID.randomUUID().toString();
            String tradeCodeKey = MallConstant.CACHE_MEMBER_MEMBERID_TRADE_CODE_PRE + memberId
                    + MallConstant.CACHE_MEMBER_MEMBERID_TRADE_CODE_SUF;
            int expireTime = 2*60*60;
            cacheJedis.setex(tradeCodeKey,expireTime,tradeCode);
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            if(cacheJedis != null){
                cacheJedis.close();
            }
        }
        return tradeCode;
    }

    @Override
    public String checkTradeCode(String memberId, String tradeCode) {
        log.debug("【memberId=】" + memberId + ",【tradeCode=】" + tradeCode);
        String ret = MallConstant.RESULT_MES_FAIL;
        String tradeCodeKey = MallConstant.CACHE_MEMBER_MEMBERID_TRADE_CODE_PRE + memberId
                + MallConstant.CACHE_MEMBER_MEMBERID_TRADE_CODE_SUF;
        Jedis cacheJedis = null;
        try{
            cacheJedis = redisUtil.getCacheJedis();
            if(StringUtils.isNotBlank(tradeCode) && cacheJedis != null){
                String tradeCodeFromCache = cacheJedis.get(tradeCodeKey);
                //使用lua脚本删除cache中数据（先查后删一次性完成）
                Long eval = this.deleteTraceCodeInCache(cacheJedis,tradeCodeKey,tradeCode);
                if (eval!=null && eval!=0) {
                    ret = MallConstant.RESULT_INFO_SUCCESS;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            cacheJedis.close();
        }
        log.debug("【ret=】" + ret);
        return ret;
    }
/**
    * @description:事务操作，删除购物车并添加订单和订单详细
    * 具体操作：遍历购物车选中数据
    * 1.验证库存
    * 2.验证价格
    * 3.删除购物车(缓存和数据库)
    * 4.添加订单和订单详细
    * @author:  YX
    * @date:    2019/12/22 9:08
    * @param: cartItemCaches
    * @return: java.lang.String success fail
    * @throws:
    */
    @Transient
    @Override

    public Order delCartItemListAndAddOrder(Long memberId,String nickName,String deliveryAddressId,List<CartItem> cartItemCaches) {
        Order retOrder = null;
        List<OrderItem> orderItems = new ArrayList<>();
        boolean continueFlage = true;
        continueFlage = this.validateCheckedPriceInCartItems(cartItemCaches);
        if(continueFlage){
             // 3.删除购物车(缓存和数据库)
            int delCacheCount = cartService.deleteCheckedCartItemsInCache(memberId,cartItemCaches);
            int delDBCount = cartService.deleteCheckedCartItems(cartItemCaches);
            // 4.添加订单和订单详细
            retOrder = this.addOrderAndOrderItems(memberId,nickName,deliveryAddressId,cartItemCaches);
        }
        return retOrder;
    }

    @Override
    public Order getOneOrder(Order paramOrder) {
        Order order = orderMapper.selectOne(paramOrder);
        return order;
    }

    private Order addOrderAndOrderItems(Long memberId,String nickName,String deliveryAddressId,List<CartItem> cartItemCaches) {
        Order order = this.buildOrderAndOrderItems(memberId,nickName,deliveryAddressId,cartItemCaches);
        int orderCount = orderMapper.insertSelective(order);
        log.debug("【orderCount=】" + orderCount);
        int orderItemCount = this.addOrderItems(order);
        log.debug("【orderItemCount=】" + orderItemCount);
        return order;
    }

    private int addOrderItems(Order order) {
        Long orderId = order.getId();
        int count = 0;
        for (OrderItem orderItem : order.getOrderItems()) {
            orderItem.setOrderId(orderId);
            orderItemMapper.insertSelective(orderItem);
            ++count;
        }
        log.debug("【count=】" + count);
        return count;
    }


    private Order buildOrderAndOrderItems(Long memberId,String nickName,String deliveryAddressId,List<CartItem> cartItemCaches) {
        Order order = new Order();
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal orderTotalAmount = BigDecimal.ZERO;
        for (CartItem cartItemCach : cartItemCaches) {
            if(MallConstant.IS_CHECKED.equals(cartItemCach.getIsChecked())){
                OrderItem orderItem = new OrderItem();
                orderItem.setProductSkuId(cartItemCach.getProductSkuId());
                orderItem.setProductName(cartItemCach.getProductName());
                orderItem.setProductId(cartItemCach.getProductId());
                orderItem.setProductCategoryId(cartItemCach.getProductCategoryId());
                orderItem.setProductPic(cartItemCach.getProductPic());
                orderItem.setProductPrice(cartItemCach.getPrice());
                orderItem.setProductQuantity(cartItemCach.getQuantity());
                cartItemCach.setTotalPrice(new BigDecimal(String.valueOf(cartItemCach.getQuantity())).
                        multiply(cartItemCach.getPrice()));
                orderTotalAmount = orderTotalAmount.add(cartItemCach.getTotalPrice());
                orderItems.add(orderItem);
            }
        }
        order.setOrderItems(orderItems);
        this.buildNewOrder(order,nickName,memberId,orderTotalAmount,deliveryAddressId);
        return order;
    }

    @Override
    public List<Order> getOrderAndItems(String orderSn){
        List<Order> orders = orderMapper.selectOrderAndItems(orderSn);
        log.debug("【orders=】" + orders);
        return orders;
    }
/**
    * @description: 同一事务：付款成功后更新订单状态并添加消息队列
    * @author:  YX
    * @date:    2019/12/28 10:15
    * @param: orderSn
    * @return: void
    * @throws:
    */
    @Override
    @Transactional
    public void updateOrderAndSendOrderPayQueue(String orderSn) {
        log.debug("【orderSn=】" + orderSn);
        int updateCount = this.updateOrderPaySuccess(orderSn);
        this.sendOrderPayMQ(orderSn);
    }

    private void sendOrderPayMQ(String orderSn) {
        log.debug("【orderSn=】" + orderSn);
        try{
            Map<String,String> map = new HashMap<>();
            List<Order> orders = orderMapper.selectOrderAndItems(orderSn);
            List<OmsOrder> omsOrders = this.buildOrderAndOrderItemVos(orders);
            ActiveMQTextMessage textMessage = new ActiveMQTextMessage();
            OmsOrder retOmsOrder = omsOrders.get(0);
            String jsonStr = JSON.toJSONString(retOmsOrder);
            textMessage.setText(jsonStr);
            log.debug("【jsonStr=】" + jsonStr);
            activeMQUtil.sendTransactedMQ(MallConstant.MQ_ORDER_PAY_QUEUE,textMessage);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
        * @description: 构建Order的Vo
        * @author:  YX
        * @date:    2019/12/31 16:09
        * @param: orders
        * @return: java.util.List<com.yx.mall.vo.OmsOrder>
        * @throws: 
        */
    public List<OmsOrder> buildOrderAndOrderItemVos(List<Order> orders) {
        log.debug("【orders=】" + orders);
        List<OmsOrder> omsOrders = null;
        try{
            if(orders!=null && !orders.isEmpty()){
                omsOrders = new ArrayList<>();
                for (Order order : orders) {
                    OmsOrder omsOrder = new OmsOrder();
                    List<OmsOrderItem> omsOrderItems= new ArrayList<>();
                    for (OrderItem orderItem : order.getOrderItems()) {
                        OmsOrderItem omsOrderItem = new OmsOrderItem();
                        log.debug("orderItem复制前=====" + orderItem);
                        setObjectIntegerValue(orderItem,OrderItem.class);//测试代码//测试代码//测试代码//测试代码//测试代码//测试代码//测试代码//测试代码
                        BeanUtils.copyProperties(omsOrderItem,orderItem);
                        log.debug("orderItem复制后=====" + orderItem);
                        log.debug("orderItem=====" + orderItem);
                        log.debug("omsOrderItem====" + omsOrderItem);
                        omsOrderItems.add(omsOrderItem);
                    }
                    setObjectIntegerValue(order,Order.class);//测试代码//测试代码//测试代码//测试代码//测试代码//测试代码//测试代码//测试代码
                    BeanUtils.copyProperties(omsOrder,order);
                    log.debug("order=====" + order);
                    log.debug("omsOrder====" + omsOrder);
                    omsOrders.add(omsOrder);
                    omsOrder.setOmsOrderItems(omsOrderItems);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        log.debug("【omsOrders=】" + omsOrders);
        return omsOrders;
    }

    private int updateOrderPaySuccess(String orderSn) {
        Example example = new Example(Order.class);
        example.createCriteria().andEqualTo("orderSn",orderSn);
        Order order = new Order();
        order.setStatus(Integer.valueOf(MallConstant.DB_MALL_ORDER_STATUS_WAIT_SEND_GOODS));
        int updateCount = orderMapper.updateByExampleSelective(order,example);
        log.debug("【updateCount=】" + updateCount);
        return updateCount;

    }

    private void buildNewOrder(Order order, String nickName, Long memberId, BigDecimal orderTotalAmount, String deliveryAddressId) {
        Date d = new Date();
        order.setMemberUsername(nickName);
        order.setMemberId(memberId);
        order.setTotalAmount(orderTotalAmount);
        order.setCreateTime(d);
        order.setModifyTime(d);
        order.setOrderSn(this.buildOrderSn(d));
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setCouponAmount(BigDecimal.ZERO);
        order.setFreightAmount(BigDecimal.ZERO);
        order.setOrderType(Integer.valueOf(MallConstant.DB_MALL_ORDER_ORDER_TYPE_NOMAL));
        order.setSourceType(Integer.valueOf(MallConstant.DB_MALL_ORDER_ORDER_TYPE_SOURCE_TYPE_PC));
        MemberReceiveAddress addr = this.getMemberReceiveAddressById(deliveryAddressId);
        order.setReceiverName(addr.getName());
        order.setReceiverCity(addr.getCity());
        order.setReceiverPhone(addr.getPhoneNumber());
        order.setReceiverDetailAddress(addr.getDetailAddress());
        order.setReceiverProvince(addr.getProvince());
        order.setStatus(Integer.parseInt(MallConstant.DB_MALL_ORDER_STATUS_WAIT_PAY));
    }

    private MemberReceiveAddress getMemberReceiveAddressById(String deliveryAddressId) {
        MemberReceiveAddress memberReceiveAddress= new MemberReceiveAddress();
        memberReceiveAddress.setId(Long.valueOf(deliveryAddressId));
        MemberReceiveAddress memberReceiveAddressDB = memberReceiveAddressService.getOneMemberReceiveAddress(memberReceiveAddress);
        return memberReceiveAddressDB;
    }


    private String buildOrderSn(Date d) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String dateStr = sdf.format(d);
        String orderSn = "mall" + dateStr + System.currentTimeMillis();
        log.debug("【orderSn=】" + orderSn);
        return orderSn;
    }

    private boolean validateCheckedPriceInCartItems(List<CartItem> cartItemCaches) {
        boolean continueFlage = true;
        for (CartItem cartItemCach : cartItemCaches) {
            //1.验证库存（未开发）
            //2.验证价格
            Long cartItemId = cartItemCach.getId();
            if(MallConstant.IS_CHECKED.equals(cartItemCach.getIsChecked()) && cartItemId != null){
                CartItem paramCartItem = new CartItem();
                paramCartItem.setId(cartItemId);
                CartItem dbCartItem = cartService.getOneCart(paramCartItem);
                if(cartItemCach.getPrice().compareTo(dbCartItem.getPrice()) != 0){
                    continueFlage = false;
                }
            }
        }
        return continueFlage;
    }

    private Long deleteTraceCodeInCache( Jedis cacheJedis,String tradeCodeKey,String tradeCode) {
        Long eval = 0L;
        String script = "if redis.call('get',KEYS[1]) == ARGV[1] " +
                            "then return redis.call('del',KEYS[1]) " +
                        "else return 0 end";
        log.debug("【script=】" + script);
        eval = (Long)cacheJedis.eval(script, Collections.singletonList(tradeCodeKey),
                Collections.singletonList(tradeCode));
        log.debug("【eval=】" + eval);
        return eval;
    }

    public static void main(String[] args) {
        OrderServiceImpl osi = new OrderServiceImpl();
        List<Order> orders = new ArrayList<>();
        Order o = new Order();
       /* o.setId(12L);
        o.setOrderSn("111");
        o.setStatus(1);
        o.setOrderType(8);
        o.setAutoConfirmDay(12);
        o.setPayType(54);*/
        setObjectIntegerValue(o,Order.class);


        OrderItem oi = new OrderItem();
        oi.setId(222L);
        oi.setProductQuantity(250);
        oi.setProductName("shouji");
        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(oi);
        setObjectIntegerValue(oi,OrderItem.class);

        o.setOrderItems(orderItems);
        orders.add(o);
        System.out.println("==========================================="+ orders);

        List<OmsOrder> omsOrders = osi.buildOrderAndOrderItemVos(orders);
        System.out.println("-------------------------------------------" + omsOrders);
    }

    private static void setObjectIntegerValue(Object o,Class c) {
        Field[] declaredFields = c.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            if(declaredField.getType().equals(Integer.class)){
                try {
                    String name = declaredField.getName();
                    declaredField.setAccessible(true);
                    Integer value = (Integer)declaredField.get(o);
                    if(value == null){
                        declaredField.set(o,0);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
