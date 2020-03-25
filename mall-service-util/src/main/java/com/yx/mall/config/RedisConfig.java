package com.yx.mall.config;

import com.yx.mall.util.RedisUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig {

    @Value("${spring.redis.cache.host:disabled}")
    private String cacheHost;

    @Value("${spring.redis.cache.port:0}")
    private int cachePort;

    @Value("${spring.redis.cache.database:0}")
    private int cacheDatabase;

    @Value("${spring.redis.lock.host:disabled}")
    private String lockHost;

    @Value("${spring.redis.lock.port:0}")
    private int lockPort;

    @Value("${spring.redis.lock.database:0}")
    private int lockDatabase;

    @Bean
    public RedisUtil getRedisUtil(){
       if(cacheHost.equals("disabled")){
           return null;
       }
       if(lockHost.equals("disabled")){
            return null;
       }
       RedisUtil redisUtil = new RedisUtil();
       redisUtil.initCachePool(cacheHost,cachePort,cacheDatabase);
       redisUtil.initLockPool(lockHost,lockPort,lockDatabase);
       return redisUtil;
    }
}
