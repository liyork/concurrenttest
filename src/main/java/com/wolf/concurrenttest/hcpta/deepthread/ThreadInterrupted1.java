package com.wolf.concurrenttest.hcpta.deepthread;

import java.util.concurrent.TimeUnit;

/**
 * Description:
 * Created on 2021/9/17 12:50 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ThreadInterrupted1 {
    public static void main(String[] args) {
        System.out.println("main thread is interrupted? " + Thread.interrupted());
        Thread.currentThread().interrupt();
        System.out.println("main thread is interrupted? " + Thread.currentThread().isInterrupted());

        try {
            // 立即被中断
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            System.out.println("i will be interrupted still");
        }
        System.out.println("main thread is interrupted? " + Thread.currentThread().isInterrupted());
    }
}
