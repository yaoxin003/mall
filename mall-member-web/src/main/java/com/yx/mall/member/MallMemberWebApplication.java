package com.yx.mall.member;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MallMemberWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(MallMemberWebApplication.class, args);
	}

}
