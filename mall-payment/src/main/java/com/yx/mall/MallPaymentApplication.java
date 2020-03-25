package com.yx.mall;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan(basePackages="com.yx.mall.payment.mapper")
@EnableDubbo
public class MallPaymentApplication {

	public static void main(String[] args) {
		SpringApplication.run(MallPaymentApplication.class, args);
	}

}
