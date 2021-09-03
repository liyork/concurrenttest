package com.wolf.concurrenttest.bfbczm.foundation;

import java.util.concurrent.TimeUnit;

/**
 * Description: 中断打断join的线程
 * Created on 2021/9/2 1:19 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class JoinInterruptTest {
    public static void main(String[] args) {
        Thread threadOne = new Thread(() -> {
            System.out.println("threadOne begin run!");
            for (; ; ) {

            }
        });

        Thread mainThread = Thread.currentThread();

        // 休眠1s，中断main线程
        Thread threadTwo = new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mainThread.interrupt();
        });

        threadOne.start();
        threadTwo.start();

        try {
            // main在等待threadOne完成，阻塞自己
            threadOne.join();
        } catch (InterruptedException e) {
            System.out.println("main thread: " + e);
        }
    }

}
