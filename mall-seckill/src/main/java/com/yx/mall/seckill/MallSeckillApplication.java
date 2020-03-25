package com.yx.mall.seckill;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.yx.mall")
public class MallSeckillApplication {

	public static void main(String[] args) {
		SpringApplication.run(MallSeckillApplication.class, args);
	}

}
