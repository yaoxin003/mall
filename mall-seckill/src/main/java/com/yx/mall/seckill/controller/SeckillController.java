package com.yx.mall.seckill.controller;

import com.yx.mall.config.RedissonConfig;
import com.yx.mall.constant.MallConstant;
import com.yx.mall.util.RedisUtil;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;
import java.util.List;

/**
 * @description:秒杀
 * @author: yx
 * @date: 2020/01/02/14:40
 */
@Controller
@Log4j
public class SeckillController {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RedissonConfig redissonConfig;
/**
    * @description: 使用Redisson进行秒杀
    * @author:  YX
    * @date:    2020/01/03 8:31
    * @param: skuId
    * @return: java.lang.String
    * @throws: 
    */    
    @RequestMapping("redissonKill")
    @ResponseBody
    public String redissonKill(Long skuId){
        String key = MallConstant.SECKILL_WARE_SKU_SKUID_STOCK_PRE + skuId
                + MallConstant.SECKILL_WARE_SKU_SKUID_STOCK_SUF;
        RedissonClient redissonClient = redissonConfig.redissonClient();
      /*  String value = redisUtil.getCacheJedis().get(key);
        log.debug("【获得库存value=】" + value);*/
        RSemaphore semaphore = redissonClient.getSemaphore(key);
        boolean b = semaphore.tryAcquire();
        if(b){
            log.debug("【semaphore=】" + semaphore);
            String value1 = redisUtil.getCacheJedis().get(key);
            log.debug("【获得库存value1=】" + value1);
        }else{
            log.debug("【库存被他人修改，秒杀失败】"  );
        }

        return "redisson秒杀";
    }

    @RequestMapping("redisKill")
    @ResponseBody
    public String redisKill(Long skuId){
        if(skuId != null){
            Jedis cacheJedis = redisUtil.getCacheJedis();
            String key = MallConstant.SECKILL_WARE_SKU_SKUID_STOCK_PRE + skuId
                    + MallConstant.SECKILL_WARE_SKU_SKUID_STOCK_SUF;
            log.debug("【key=】" + key);
            String watch = cacheJedis.watch(key);//watch
            log.debug("【watch=】" + watch);
            String value = cacheJedis.get(key);
            log.debug("【value=】" + value);
            if(StringUtils.isNotBlank(value)){
                long stock = Long.parseLong(value);//库存
                log.debug("【stock=】" + stock);
                Transaction multi = null;
                if(stock > 0){
                    multi = cacheJedis.multi();//multi
                    Response<Long> longResponse = multi.decrBy(key, 1);//decr
                    log.debug("库存为【longResponse=】" + longResponse);
                }else{
                    log.debug("库存已用尽【stock】=" + stock);
                }
                List<Object> exec = multi.exec();//exec
                log.debug("【exc=】" + exec);
                if(exec != null && exec.size()>0){
                    //发送订单消息队列
                    log.debug("【发送订单消息队列】exec.size()=" + exec.size() + ",exec=" + exec);
                }else{
                    log.debug("【库存被他人修改，秒杀失败】");
                }
                cacheJedis.close();
            }
        }
        return "redis秒杀";
    }




}
