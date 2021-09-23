package com.wolf.concurrenttest.hcpta.singleton;

/**
 * Description: 枚举方式
 * Created on 2021/9/23 9:05 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public enum Singleton6 {  // 枚举类型是final，不允许被继承
    INSTANCE;
    private byte[] data = new byte[1024];

    Singleton6() {
        System.out.println("INSTANCE will be initialized immediately");
    }

    // 调用此方法会主动使用Singleton6，INSTANCE将会被实例化
    public static void method() {
        System.out.println("test method");
    }

    public static Singleton6 getInstance() {
        return INSTANCE;
    }
}
