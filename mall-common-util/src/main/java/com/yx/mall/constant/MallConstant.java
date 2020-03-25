package com.yx.mall.constant;

public class MallConstant {

        /**-----------------------------------------mall网址-----------------------------------------**/
        public static final String MALL_SERACH_URI = "http://search.mall.com:8083/index";
        public static final String MALL_PASSPORT_VLOGIN_URI = "http://passport.mall.com:8085/vlogin";
        public static final String MALL_PAYMENT_URI = "http://payment.mall.com:8087/index";


        /**---------------------------数据库字段 关系数据库 库名 表名 字段名 常量---------------------------**/
        public static final String DB_MALL_ORDER_STATUS_WAIT_PAY = "0";//待付款
        public static final String DB_MALL_ORDER_STATUS_WAIT_SEND_GOODS = "1";//待发货

        public static final String DB_MALL_ORDER_ORDER_TYPE_NOMAL = "0";//正常订单
        public static final String DB_MALL_ORDER_ORDER_TYPE_SECKILL= "1";//秒杀订单

        public static final String DB_MALL_ORDER_ORDER_TYPE_SOURCE_TYPE_PC = "0";//PC订单
        public static final String DB_MALL_ORDER_ORDER_TYPE_SOURCE_TYPE_APP = "1";//APP订单

        public static final String DB_MALL_MEMBER_SOURCE_TYPE_MALL = "1";//MALL商场网址用户（本网站用户）
        public static final String DB_MALL_MEMBER_SOURCE_TYPE_WEIBO = "2";//微博用户

        public static final String DB_MALL_PAYMENT_STATUS_NO_PAY = "未付款";
        public static final String DB_MALL_PAYMENT_STATUS_PAY_SUCCESS = "付款成功";


        /**-----------------------------------------代码标识-----------------------------------------**/
        /**购物车明细选中/订单明细选中**/
        public static final String IS_CHECKED = "1";

        public static final String RESULT_INFO_SUCCESS = "success";
        public static final String RESULT_MES_FAIL = "fail";


        /**-----------------------------------------cache-----------------------------------------**/
        //sku
        public static final String CACHE_SKU_SKUID_INFO_PRE = "sku:";
        public static final String CACHE_SKU_SKUID_INFO_SUF = ":info";
        //购物车
        public static final String CACHE_USER_MEMBERID_CARD_PRE = "user:";
        public static final String CACHE_USER_MEMBERID_CARD_SUF = ":card";
        //用户密码
        public static final String CACHE_MEMBER_USERNAME_PASSWORD_PRE = "member:";
        public static final String CACHE_MEMBER_USERNAME_PASSWORD_SUF = ":password";
        //用户信息
        public static final String CACHE_MEMBER_USERNAME_PASSWORD_INFO_PRE = "member:";
        public static final String CACHE_MEMBER_USERNAME_PASSWORD_INFO_SUF = ":info";
        //用户订单tradeCode
        public static final String CACHE_MEMBER_MEMBERID_TRADE_CODE_PRE = "member:";
        public static final String CACHE_MEMBER_MEMBERID_TRADE_CODE_SUF = ":tradeCode";

        /**-----------------------------------------秒杀-----------------------------------------**/
        public static final String SECKILL_WARE_SKU_SKUID_STOCK_PRE = "seckill:waresku:";
        public static final String SECKILL_WARE_SKU_SKUID_STOCK_SUF = ":stock";

        /**-----------------------------------------cache lock-----------------------------------------**/
        //sku
        public static final String CACHE_LOCK_SKU_SKUID_INFO_PRE = "sku:";
        public static final String CACHE_LOCK_SKU_SKUID_INFO_SUF = ":info:lock";
        //member
        public static final String CACHE_LOCK_MEMBER_USERNAME_PASSWORD_INFO_PRE = "member:";
        public static final String CACHE_LOCK_MEMBER_USERNAME_PASSWORD_INFO_SUF = ":info:lock";


        /**-----------------------------------------cookie-----------------------------------------**/
        //购物车
        public static final String COOKIE_CART_LIST_COOKIE = "cart_list_cookie";
        public static final String JWT_SECRET_KEY = "jwt20191215mall";


        /**-----------------------------------------jwt-----------------------------------------**/
        public static final String LOGIN_TOKEN_STATUS_SUCCESS = "success";
        public static final String LOGIN_TOKEN_STATUS_FAIL = "fail";


        /**-----------------------------------------微博开放平台-----------------------------------------**/
        public static final String WEIBO_CLIENT_ID = "178828987";//App Key
        public static final String WEIBO_APP_SECRET = "ade88c33dfa2152c5ccb3d6918e8372b";
        public static final String WEIBO_CALLBACK_URL = "http://passport.mall.com:8085/vlogin";
        public static final String WEIBO_AUTHORIZE_URL = "https://api.weibo.com/oauth2/authorize";
        public static final String WEIBO_ACCESS_TOKE_URL = "https://api.weibo.com/oauth2/access_token";
        public static final String WEIBO_SHOW_JSON_URL = "https://api.weibo.com/2/users/show.json";


        /**-----------------------------------------消息队列-----------------------------------------**/
        public static final String MQ_PAYMENT_SUCCESS_QUEUE = "MQ_PAYMENT_SUCCESS_QUEUE";
        public static final String MQ_ORDER_PAY_QUEUE = "MQ_ORDER_PAY_QUEUE";
        public static final String MQ_PAYMENT_CHECK_QUEUE = "MQ_PAYMENT_CHECK_QUEUE";
        public static final int MQ_DELAY_TIME = 1* 60 * 1000; //延迟发送消息时间
        public static final int MQ_DELAY_SEND_COUNT = 5; //延迟消息发送次数

        /**-----------------------------------------支付宝参数-----------------------------------------**/
        public static final String ALIPAY_TRADE_QUERY_RESPONSE_TRADE_SUCCESS = "TRADE_SUCCESS";
}
