package com.mohsen.beanLifeCycle.service;

import com.mohsen.beanLifeCycle.tx.MiniTransactional;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

    @Override
    @MiniTransactional
    public void placeOrder() {

        System.out.println("اجرای متد placeOrder");

        // برای تست rollback این خط را فعال کن
        // throw new RuntimeException("خطا!");
    }
}
