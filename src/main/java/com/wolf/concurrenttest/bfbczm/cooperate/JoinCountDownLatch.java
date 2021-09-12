package com.wolf.concurrenttest.bfbczm.cooperate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Description: 用CountDownLatch模拟join行为
 * Created on 2021/9/11 3:26 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class JoinCountDownLatch {
    private static volatile CountDownLatch countDownLatch = new CountDownLatch(2);

    public static void main(String[] args) throws InterruptedException {
        Thread threadOne = new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                countDownLatch.countDown();
            }
            System.out.println("child threadOne over!");
        });

        Thread threadTwo = new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                countDownLatch.countDown();  // 内部计数器-1
            }
            System.out.println("child threadTwo over!");
        });

        threadOne.start();
        threadTwo.start();

        System.out.println("wait all child thread over!");

        // 等待子线程执行完毕，才返回
        countDownLatch.await();  // 阻塞，直到计数器为0，才返回

        System.out.println("all child thread over!");
    }
}
