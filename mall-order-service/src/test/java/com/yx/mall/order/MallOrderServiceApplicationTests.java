package com.yx.mall.order;


import com.yx.mall.bean.Order;
import com.yx.mall.order.mapper.OrderMapper;
import com.yx.mall.service.OrderService;
import lombok.extern.log4j.Log4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest
@Log4j
public class MallOrderServiceApplicationTests {

	@Autowired
	private OrderMapper orderMapper;

	@Autowired
	private OrderService OrderService;

	@Test
	public void contextLoads() {

		List<Order> orders = orderMapper.selectOrderAndItems("mall202001010919056381577841545639");

		System.out.println("==========================================="+ orders);
		OrderService.buildOrderAndOrderItemVos(orders);

	}

}
