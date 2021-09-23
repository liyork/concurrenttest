package com.wolf.concurrenttest.hcpta.volatiletest;

import java.util.concurrent.CountDownLatch;

/**
 * Description:
 * Created on 2021/9/23 6:52 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class VolatileTest {
    private static volatile int i = 0;
    private static final CountDownLatch latch = new CountDownLatch(10);

    private static void inc() {
        i++;
    }

    public static void main(String[] args) throws InterruptedException {
        for (int j = 0; j < 10; j++) {
            new Thread(() -> {
                for (int x = 0; x < 1000; x++) {
                    inc();
                }
                latch.countDown();
            }).start();
        }

        latch.await();
        System.out.println(i);
    }
}
