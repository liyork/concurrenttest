package com.wolf.concurrenttest.hcpta.singleton;

/**
 * Description: 懒汉式
 * 多线程环境下有问题
 * Created on 2021/9/23 9:05 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public final class Singleton1 {  // 不允许被继承
    private byte[] data = new byte[1024];

    // 类变量，仅定义
    private static Singleton1 instance = null;

    // 私有构造函数
    private Singleton1() {

    }

    public static Singleton1 getInstance() {
        if (null == instance) {
            instance = new Singleton1();
        }
        return instance;
    }
}
