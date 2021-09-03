package com.wolf.concurrenttest.bfbczm.foundation;

/**
 * Description: 中断方法的不同，测试
 * Created on 2021/9/2 6:58 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class InterruptDiff {
    public static void main(String[] args) throws InterruptedException {
        Thread threadOne = new Thread(() -> {
            for (; ; ) {

            }
        });

        threadOne.start();
        // 设定中断标志
        threadOne.interrupt();
        // 获取中断标志
        System.out.println("isInterrupted: " + threadOne.isInterrupted());
        // 获取中断标志并重置(这是对当前线程，即main)
        System.out.println("isInterrupted " + threadOne.interrupted());
        // 获取中断标志并重置(这是对当前线程，即main)
        System.out.println("isInterrupted " + Thread.interrupted());
        // 获取中断标志
        System.out.println("isInterrupted " + threadOne.isInterrupted());

        threadOne.join();

        System.out.println("main thread is over");
    }
}
