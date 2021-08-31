package com.wolf.concurrenttest.taojcp.tools;

import java.util.concurrent.CountDownLatch;

/**
 * Description: CountDownLatch测试，await能执行前提是，指定数量的countDown执行
 * Created on 2021/8/31 1:00 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class CountDownLatchTest {
    static CountDownLatch c = new CountDownLatch(2);

    public static void main(String[] args) throws InterruptedException {
        new Thread(() -> {
            System.out.println(1);
            c.countDown();
            System.out.println(2);
            c.countDown();
        }).start();

        c.await();
        System.out.println(3);
    }
}
