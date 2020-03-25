package com.yx.mall.search;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@EnableDubbo
public class MallSearchServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MallSearchServiceApplication.class, args);
	}

}
