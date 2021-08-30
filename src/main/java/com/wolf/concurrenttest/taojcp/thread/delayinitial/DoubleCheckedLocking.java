package com.wolf.concurrenttest.taojcp.thread.delayinitial;

/**
 * Description: 延迟初始化，用volatile+双重检查锁
 * Created on 2021/8/26 7:03 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class DoubleCheckedLocking {
    private static volatile Instance instance;

    public static Instance getInstance() {
        if (instance == null) {
            synchronized (DoubleCheckedLocking.class) {
                if (instance == null) {
                    instance = new Instance();
                }
            }
        }
        return instance;
    }
}
