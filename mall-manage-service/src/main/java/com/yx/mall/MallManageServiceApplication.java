package com.yx.mall;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan(basePackages= {"com.yx.mall.*.mapper"})
@EnableDubbo
@EnableTransactionManagement
public class MallManageServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MallManageServiceApplication.class, args);
	}

}
