package com.wolf.concurrenttest.mtadp.latch;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/05
 */
public class MyCountDownLatchTest {

    public static void main(String[] args) throws InterruptedException {

//        testBase();
        testTimeout();
    }

    private static void testBase() throws InterruptedException {
        MyLatch myLatch = new MyCountDownLatch(4);

        IntStream.range(0, 4).forEach(i -> new Thread(() -> {

            try {
                TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(10));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread() + " is running.");

            myLatch.countDown();
        }).start());

        System.out.println("main is await..");
        myLatch.await();
        System.out.println("main is running...");
    }

    private static void testTimeout() {

        MyLatch myLatch = new MyCountDownLatch(4);

        IntStream.range(0, 4).forEach(i -> new Thread(() -> {

            try {
                TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(10));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread() + " is running.");

            myLatch.countDown();
        }).start());

        System.out.println("main is await..");
        try {
            myLatch.await(TimeUnit.SECONDS, 4);
        } catch (InterruptedException e) {
            e.printStackTrace();//收到超时，再决定下一步动作(继续还是放弃还是重试)。
        }
        System.out.println("main is running...");
    }
}
