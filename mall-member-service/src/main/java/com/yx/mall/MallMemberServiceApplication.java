package com.yx.mall;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import tk.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@MapperScan(basePackages = "com.yx.mall.member.mapper")
@EnableDubbo
public class MallMemberServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MallMemberServiceApplication.class, args);
	}

}
