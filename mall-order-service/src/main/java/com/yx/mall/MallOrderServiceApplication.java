package com.yx.mall;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@EnableDubbo
@MapperScan(basePackages = {"com.yx.mall.order.mapper"})
public class MallOrderServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MallOrderServiceApplication.class, args);
	}

}
