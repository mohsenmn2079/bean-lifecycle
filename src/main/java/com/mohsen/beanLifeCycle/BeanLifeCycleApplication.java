package com.mohsen.beanLifeCycle;

import com.mohsen.beanLifeCycle.service.OrderService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class BeanLifeCycleApplication {

	public static void main(String[] args) {

        ApplicationContext ctx = SpringApplication.run(BeanLifeCycleApplication.class, args);

        OrderService orderService = ctx.getBean(OrderService.class);

        orderService.placeOrder();	}

}
