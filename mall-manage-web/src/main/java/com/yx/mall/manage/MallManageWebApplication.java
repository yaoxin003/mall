package com.yx.mall.manage;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MallManageWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(MallManageWebApplication.class, args);
	}

}
