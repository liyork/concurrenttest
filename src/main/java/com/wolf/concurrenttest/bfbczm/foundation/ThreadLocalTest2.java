package com.wolf.concurrenttest.bfbczm.foundation;

/**
 * Description: 测试tl不可继承性
 * Created on 2021/9/3 9:12 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ThreadLocalTest2 {
    public static ThreadLocal<String> threadLocal = new ThreadLocal<>();

    public static void main(String[] args) {
        // 设定main线程的本地变量
        threadLocal.set("hello world");

        Thread thread = new Thread(() -> {
            // 子线程输出本地线程变量的值
            System.out.println("thread:" + threadLocal.get());
        });
        thread.start();
        // 主线程输出线程本地变量值
        System.out.println("main:" + threadLocal.get());
    }
}
