package com.wolf.concurrenttest.hcpta.singleton;

/**
 * Description: Holder方式
 * 在Singleton5类的初始化过程中不会创建Singleton5的实例，
 * 当Holder被主动引用时，会创建Singleton5的实例
 * Singleton5实例的创建过程再java程序编译时期收集至<clinit>()方法中，是同步方法，可以保证内存的可见性、
 * jvm指令的顺序性和原子性
 * Created on 2021/9/23 9:05 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public final class Singleton5 {  // 不允许被继承
    private byte[] data = new byte[1024];

    private Singleton5() {
    }

    // 静态内部类中持有Singleton5的实例，可被直接初始化
    private static class Holder {
        private static Singleton5 instance = new Singleton5();
    }

    public static Singleton5 getInstance() {
        return Holder.instance;
    }
}
