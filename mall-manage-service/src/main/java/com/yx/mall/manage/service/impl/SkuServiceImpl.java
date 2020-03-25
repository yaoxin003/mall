package com.yx.mall.manage.service.impl;

import com.alibaba.fastjson.JSON;
import com.yx.mall.bean.SkuAttrValue;
import com.yx.mall.bean.SkuImage;
import com.yx.mall.bean.SkuInfo;
import com.yx.mall.bean.SkuSaleAttrValue;
import com.yx.mall.constant.MallConstant;
import com.yx.mall.manage.mapper.SkuAttrValueMapper;
import com.yx.mall.manage.mapper.SkuImageMapper;
import com.yx.mall.manage.mapper.SkuInfoMapper;
import com.yx.mall.manage.mapper.SkuSaleAttrValueMapper;
import com.yx.mall.service.SkuService;
import com.yx.mall.util.RedisUtil;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Log4j
@Service
public class SkuServiceImpl implements SkuService {

    @Autowired
    private SkuInfoMapper skuInfoMapper;

    @Autowired
    private SkuImageMapper skuImageMapper;

    @Autowired
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;

    @Autowired
    private SkuAttrValueMapper skuAttrValueMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RedissonClient redissonClient;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveSkuInfo(SkuInfo skuInfo) {

        log.debug("【skuInfo=】" + skuInfo);
        List<SkuImage> skuImages = skuInfo.getSkuImageList();
        if(skuImages == null || skuImages.isEmpty()){
            throw new RuntimeException("SkuImageList为空");
        }
        List<SkuAttrValue> skuAttrValues = skuInfo.getSkuAttrValueList();
        if(skuAttrValues == null || skuAttrValues.isEmpty() ){
            throw new RuntimeException("SkuAttrValueList为空");
        }
        List<SkuSaleAttrValue> skuSaleAttrValues = skuInfo.getSkuSaleAttrValueList();
        if(skuSaleAttrValues == null || skuSaleAttrValues.isEmpty()){
            throw new RuntimeException("SkuSaleAttrValueList为空");
        }
        int skuInfoCount = skuInfoMapper.insertSelective(skuInfo);
        if(skuInfoCount > 0){
            Long skuId = skuInfo.getId();
            for (SkuImage skuImage : skuImages) {
                skuImage.setSkuId(skuId);
                skuImageMapper.insert(skuImage);
            }
            for (SkuAttrValue skuAttrValue : skuAttrValues) {
                skuAttrValue.setSkuId(skuId);
                skuAttrValueMapper.insert(skuAttrValue);
            }
            for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValues ) {
                skuSaleAttrValue.setSkuId(skuId);
                skuSaleAttrValueMapper.insert(skuSaleAttrValue);
            }
        }
    }

    @Override
    public SkuInfo getSkuInfoByIdFromDBAndCache(Long skuId) {
        SkuInfo skuInfo = null;
        skuInfo = this.getSkuInfoByIdFromRedisson(skuId);
        return skuInfo;
    }

    /**
     * 使用Redisson锁，保证并发
     * @param skuId
     * @return
     */
    private SkuInfo getSkuInfoByIdFromRedisson(Long skuId){
        SkuInfo skuInfo = null;
        Jedis cacheJedis = null;
        String skuRedisKey = MallConstant.CACHE_SKU_SKUID_INFO_PRE + String.valueOf(skuId)
                + MallConstant.CACHE_SKU_SKUID_INFO_SUF;
        String skuRedisKeyLock = MallConstant.CACHE_LOCK_SKU_SKUID_INFO_PRE
                + String.valueOf(skuId) + MallConstant.CACHE_LOCK_SKU_SKUID_INFO_SUF;
        cacheJedis = redisUtil.getCacheJedis();
        RLock lock = redissonClient.getLock(skuRedisKeyLock);
        try{

            //先从缓存中获得数据
            skuInfo = getSkuInfoByKeyFromRedis(cacheJedis,skuRedisKey);
            //若缓存中不存在
            if(skuInfo == null){
                //用Redisson上锁
                lock.lock();
                log.debug("【Redisson上锁】skuRedisKey="+ skuRedisKey);
                skuInfo = getSkuInfoById(skuId);//从数据库查询
                Thread.sleep(1000*20);//为了测试自旋效果，休眠一会儿
                if(skuInfo != null){//数据库中存在该数据
                    //将查询结果的json值放入缓存。
                    cacheJedis.set(skuRedisKey, JSON.toJSONString(skuInfo));
                    log.debug("【查询数据库后，将值存入Redis中】skuRedisKey="+skuRedisKey);
                }else{
                    //将""放入缓存，失效时间为60秒（防止缓存穿透）
                    cacheJedis.setex(skuRedisKey,60,"");
                    log.debug("【数据库中没有该记录，将空串插入Redis中，并设置失效时间】skuRedisKey="+skuRedisKey);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            this.closeRedis(cacheJedis,lock,skuRedisKey);
        }
        return skuInfo;
    }

    /**
     * 使用Redis数据库锁，保证并发
     * @param skuId
     * @return
     */
    private SkuInfo getSkuInfoByIdFromRedisLock(Long skuId){
        SkuInfo skuInfo = null;
        Jedis cacheJedis = null;
        Jedis lockJedis = null;
        try{
            cacheJedis = redisUtil.getCacheJedis();
            lockJedis = redisUtil.getLockJedis();
            String skuCacheKey = MallConstant.CACHE_SKU_SKUID_INFO_PRE + String.valueOf(skuId)
                    + MallConstant.CACHE_SKU_SKUID_INFO_SUF;
            String skuCacheLockKey = MallConstant.CACHE_LOCK_SKU_SKUID_INFO_PRE + String.valueOf(skuId)
                    + MallConstant.CACHE_LOCK_SKU_SKUID_INFO_SUF;
            //先从缓存中获得数据
            skuInfo = getSkuInfoByKeyFromRedis(cacheJedis,skuCacheKey);
            //若缓存中不存在
            if(skuInfo == null){
                //先判断是否缓存中key是否上锁
                int redisLockTime = 30*1000;
                String redisLockValueToken = UUID.randomUUID().toString();//保证不会删除其他用用户的Lock
                String redisLockRes = lockJedis.set(skuCacheLockKey,redisLockValueToken,"nx","px",redisLockTime);
                if(StringUtils.isNotBlank(redisLockRes) && "OK".equals(redisLockRes)){//上锁成功
                    log.debug("【Cache上锁】skuCacheKey="+ skuCacheKey);
                    skuInfo = getSkuInfoById(skuId);//从数据库查询

                    //Thread.sleep(1000*20);//为了测试自旋效果，休眠一会儿

                    if(skuInfo != null){//数据库中存在该数据
                        //将查询结果的json值放入缓存。
                        cacheJedis.set(skuCacheKey, JSON.toJSONString(skuInfo));
                        log.debug("【查询数据库后，将值存入Cache中】skuCacheKey="+skuCacheKey);
                    }else{
                        //将""放入缓存，失效时间为60秒（防止缓存穿透）
                        cacheJedis.setex(skuCacheKey,60,"");
                        log.debug("【数据库中没有该记录，将空串插入Redis中，并设置失效时间】skuCacheKey="+skuCacheKey);
                    }
                    //使用lua脚本删除redis分布式锁（一步完成查询锁存在并立刻删除防止并发问题）
                    this.delCacheLockByLua(lockJedis,skuCacheLockKey,redisLockValueToken);
                    log.debug("【删除Cache锁】skuCacheKey="+ skuCacheKey);
                }else{//上锁失败，则当前休眠后自旋。
                    log.debug("【Cache上锁失败，开始休眠】skuCacheKey="+skuCacheKey);
                    //Thread.sleep(1000* 15);
                    log.debug("【Cache上锁失败，休眠结束，准备自旋】");
                    return getSkuInfoByIdFromDBAndCache(skuId);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            this.closeRedis(cacheJedis,lockJedis);
        }
        return skuInfo;
    }

    /**
     * 先查询再删除（一步完成）
     * @param skuCacheLockKey
     */
    private void delCacheLockByLua(Jedis lockJedis, String skuCacheLockKey,String redisLockValueToken) {
        log.debug("【skuCacheLockKey=】" + skuCacheLockKey + "【redisLockValueToken=】"+ redisLockValueToken);
        String script = "if redis.call('get',KEYS[1])==ARGV[1] " +
                " then return redis.call('del',KEYS[1]) else return 0 end ";
        log.debug("【delCacheLua=】" + script);
        Object delRetLua = lockJedis.eval(script, Collections.singletonList(skuCacheLockKey),Collections.singletonList(redisLockValueToken));
        log.debug("【delRetLua=】" + delRetLua);
    }

    private void closeRedis(Jedis cacheJedis, Jedis lockJedis) {
        try{
            if(cacheJedis != null){
                cacheJedis.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        try{
            if(lockJedis != null){
                lockJedis.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void closeRedis(Jedis cacheJedis, RLock lock,String skuRedisKey) {
        try{
            if(lock != null){
                lock.unlock();
                log.debug("【Redisson解锁】skuRedisKey="+ skuRedisKey);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        try{
            if(cacheJedis != null){
                cacheJedis.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 从缓存中查询数据
     * @param jedis
     * @param skuRedisKey
     * @return
     */
    private SkuInfo getSkuInfoByKeyFromRedis(Jedis jedis,String skuRedisKey) {
        SkuInfo resSkuInfo = null;
        try{

            String redisValueStr = jedis.get(skuRedisKey);
            if(StringUtils.isNotBlank (redisValueStr)){
                resSkuInfo = JSON.parseObject(redisValueStr,SkuInfo.class);
            }
            log.debug("【from Redis skuInfo=】" + resSkuInfo);
        }catch(Exception e){
            e.printStackTrace();
        }

        return resSkuInfo;
    }

    @Override
    public SkuInfo getSkuInfoById(Long skuId) {

        SkuInfo skuInfoPamam = new SkuInfo();
        skuInfoPamam.setId(skuId);
        SkuInfo skuInfo = skuInfoMapper.selectOne(skuInfoPamam);

        SkuImage skuImage = new SkuImage();
        skuImage.setSkuId(skuId);
        List<SkuImage> skuImages = skuImageMapper.select(skuImage);

        skuInfo.setSkuImageList(skuImages);
        log.debug("【from DB skuInfo=】" + skuInfo);
        return skuInfo;
    }

    public List<SkuInfo> getSkuSaleAttrValueListBySpu(Long spuId){
        List<SkuInfo> skuInfos = skuInfoMapper.selectSkuSaleAttrValueListBySpu(spuId);
        log.debug("【skuInfos=】" + skuInfos);
        return skuInfos;
    }

    public List<SkuInfo> getAllSkuInfoList(){
        List<SkuInfo> skuInfos = skuInfoMapper.selectAll();
        for (SkuInfo skuInfo : skuInfos) {
            SkuAttrValue skuAttrValue = new SkuAttrValue();
            skuAttrValue.setSkuId(skuInfo.getId());
            List<SkuAttrValue> skuAttrValues = skuAttrValueMapper.select(skuAttrValue);
            skuInfo.setSkuAttrValueList(skuAttrValues);
        }
        log.debug("【skuInfos=】"+ skuInfos);
        return skuInfos;
    }
}
