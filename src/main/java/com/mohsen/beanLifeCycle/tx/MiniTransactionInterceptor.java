package com.mohsen.beanLifeCycle.tx;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class MiniTransactionInterceptor implements InvocationHandler {

    private final Object target;
    private final MiniTransactionManager txManager;

    public MiniTransactionInterceptor(Object target, MiniTransactionManager txManager) {
        this.target = target;
        this.txManager = txManager;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if (method.isAnnotationPresent(MiniTransactional.class)) {

            txManager.begin();

            try {
                Object result = method.invoke(target, args);
                txManager.commit();
                return result;
            } catch (Exception e) {
                txManager.rollback();
                throw e;
            }
        }

        return method.invoke(target, args);
    }
}
