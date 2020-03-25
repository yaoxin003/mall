package com.yx.mall;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@EnableDubbo
@MapperScan(basePackages = {"com.yx.mall.cart.mapper"})
public class MallCartServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MallCartServiceApplication.class, args);
	}

}
