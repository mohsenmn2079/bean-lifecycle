package com.mohsen.beanLifeCycle.tx;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Proxy;

@Component
public class MiniTransactionalBeanPostProcessor implements BeanPostProcessor {

    private final MiniTransactionManager txManager = new MiniTransactionManager();

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        // فقط Beanهایی که interface دارند (برای JDK Proxy)
        Class<?> beanClass = bean.getClass();

        boolean hasTransactionalMethod = false;

        for (var method : beanClass.getMethods()) {
            if (method.isAnnotationPresent(MiniTransactional.class)) {
                hasTransactionalMethod = true;
                break;
            }
        }

        if (!hasTransactionalMethod) {
            return bean;
        }

        System.out.println("🔄 ساخت Proxy برای Bean: " + beanName);

        return Proxy.newProxyInstance(
                beanClass.getClassLoader(),
                beanClass.getInterfaces(),
                new MiniTransactionInterceptor(bean, txManager)
        );
    }
}