package com.wolf.concurrenttest.taojcp.tools;

/**
 * Description: join方式实现等待
 * Created on 2021/8/31 12:55 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class JoinCountDownLatchTest {
    public static void main(String[] args) throws InterruptedException {
        Thread parser1 = new Thread(() -> {
            System.out.println("parser1");
        });

        Thread parser2 = new Thread(() -> {
            System.out.println("parser2");
        });

        parser1.start();
        parser2.start();

        parser1.join();
        parser2.join();
        System.out.println("all parser finish");
    }
}
