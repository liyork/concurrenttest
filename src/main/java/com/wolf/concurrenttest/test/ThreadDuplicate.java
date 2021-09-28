package com.wolf.concurrenttest.test;

import java.util.concurrent.TimeUnit;

/**
 * Description: 名字相同的线程会共存
 * Created on 2021/9/27 4:14 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ThreadDuplicate {
    public static void main(String[] args) throws InterruptedException {

        Thread thread = new Thread(() -> {
            System.out.println("1111");
            try {
                TimeUnit.SECONDS.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "xxxx1");
        thread.start();

        TimeUnit.SECONDS.sleep(2);
        System.out.println("22222222222");
//    thread.interrupt()

        Thread thread2 = new Thread(() -> {
            System.out.println("33333");
            try {
                TimeUnit.SECONDS.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "xxxx1");
        thread2.start();

        Thread.currentThread().join();
    }
}
