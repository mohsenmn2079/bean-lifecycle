package com.mohsen.beanLifeCycle.tx;

public class MiniTransactionManager {

    public void begin() {
        System.out.println("[TX] BEGIN");
    }

    public void commit() {
        System.out.println("[TX] COMMIT");
    }

    public void rollback() {
        System.out.println("[TX] ROLLBACK");
    }
}
