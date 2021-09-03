package com.wolf.concurrenttest.bfbczm.foundation;

import java.util.concurrent.TimeUnit;

/**
 * Description: 中断标志位测试
 * Created on 2021/9/2 1:55 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class InterruptFlag {
    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(() -> {
            // 检查当前线程中标志
            // 若当前线程未被中断则一直执行
            while (!Thread.currentThread().isInterrupted()) {
                System.out.println(Thread.currentThread() + " hello");
            }
        });

        thread.start();

        TimeUnit.SECONDS.sleep(1);

        System.out.println("main thread interrupt thread");
        thread.interrupt();

        thread.join();
        System.out.println("main is over");
    }
}
