package com.yx.mall.manage;

import com.yx.mall.bean.ProductSaleAttr;
import com.yx.mall.manage.mapper.ProductSaleAttrMapper;
import com.yx.mall.util.RedisUtil;
import lombok.extern.log4j.Log4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.Jedis;

import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
@Log4j
public class MallManageServiceApplicationTests {

	@Autowired
	private ProductSaleAttrMapper productSaleAttrMapper;

	@Autowired
	private RedisUtil redisUtil;

	@Test
	public void contextLoads() {
		List<ProductSaleAttr> productSaleAttrs = productSaleAttrMapper.
				selectProductSaleAttrAndValueListBySku(25L,24L);
		log.debug("【productSaleAttrs=】" + productSaleAttrs);

	}

	@Test
	public void testRedis(){
		try{
			Jedis cacheJedis = redisUtil.getCacheJedis();
			cacheJedis.set("test:1:info","test");
			System.out.println("【cacheJedis=】" + cacheJedis);

			Jedis lockJedis = redisUtil.getLockJedis();
			lockJedis.set("test:1:lock","lock");
			System.out.println("【lockJedis=】" + lockJedis);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
