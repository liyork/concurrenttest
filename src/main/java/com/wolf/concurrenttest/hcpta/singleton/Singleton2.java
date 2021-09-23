package com.wolf.concurrenttest.hcpta.singleton;

/**
 * Description: 懒汉式 + 同步方法
 * 懒加载，线程安全，但synchronized排他性导致getInstance只能在同一时刻被一个线程访问，性能低
 * Created on 2021/9/23 9:05 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public final class Singleton2 {  // 不允许被继承
    private byte[] data = new byte[1024];

    // 类变量，仅定义
    private static Singleton2 instance = null;

    // 私有构造函数
    private Singleton2() {

    }

    // 同步方法，每次只能有一个线程进入
    public static synchronized Singleton2 getInstance() {
        if (null == instance) {
            instance = new Singleton2();
        }
        return instance;
    }
}
