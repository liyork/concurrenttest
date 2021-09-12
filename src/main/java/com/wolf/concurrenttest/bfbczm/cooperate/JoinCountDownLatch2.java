package com.wolf.concurrenttest.bfbczm.cooperate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Description: 一般用ExecutorService线程池管理线程，
 * 这时没办法直接调用运行线程的join，只能用CountDownLatch
 * Created on 2021/9/11 3:26 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class JoinCountDownLatch2 {
    private static volatile CountDownLatch countDownLatch = new CountDownLatch(2);

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.submit(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                countDownLatch.countDown();
            }
            System.out.println("child threadOne over!");
        });

        executorService.submit(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                countDownLatch.countDown();  // 内部计数器-1
            }
            System.out.println("child threadTwo over!");
        });

        System.out.println("wait all child thread over!");

        // 等待子线程执行完毕，才返回
        countDownLatch.await();  // 阻塞，直到计数器为0，才返回

        System.out.println("all child thread over!");
        executorService.shutdown();
    }
}
