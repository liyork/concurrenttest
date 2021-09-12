package com.wolf.concurrenttest.bfbczm.cooperate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * Description: 模拟CyclicBarrier功能
 * 相反的思路，栅栏处用acquire，认知执行时用release
 * Created on 2021/9/12 6:32 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class SemaphoreTest2 {
    private static volatile Semaphore semaphore = new Semaphore(0);

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        executorService.submit(() -> {
            try {
                System.out.println(Thread.currentThread() + " A task over");
                semaphore.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        executorService.submit(() -> {
            try {
                System.out.println(Thread.currentThread() + " A task over");
                semaphore.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // 等待子线程执行任务A完毕，返回
        semaphore.acquire(2);

        executorService.submit(() -> {
            try {
                System.out.println(Thread.currentThread() + " B task over");
                semaphore.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        executorService.submit(() -> {
            try {
                System.out.println(Thread.currentThread() + " B task over");
                semaphore.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // 等待子线程执行任务B完毕，返回
        semaphore.acquire(2);

        System.out.println("task is over");

        executorService.shutdown();
    }
}
