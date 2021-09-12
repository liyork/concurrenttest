package com.wolf.concurrenttest.bfbczm.cooperate;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Description: 计数器到0，执行构造函数中的任务，然后退出屏障点，唤醒被阻塞的线程
 * Created on 2021/9/11 4:25 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class CyclicBarrierTest1 {
    // 参数：1,计数器初始值。2,当计数器值为0时需要执行的任务。
    private static CyclicBarrier cyclicBarrier = new CyclicBarrier(2, () -> {
        System.out.println(Thread.currentThread() + " task1 merge result");
    });

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.submit(() -> {
            try {
                System.out.println(Thread.currentThread() + " task1-1");
                System.out.println(Thread.currentThread() + " enter in barrier");
                cyclicBarrier.await();  // 计数器值-1
                System.out.println(Thread.currentThread() + " enter out barrier");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        executorService.submit(() -> {
            try {
                System.out.println(Thread.currentThread() + " task1-2");
                System.out.println(Thread.currentThread() + " enter in barrier");
                cyclicBarrier.await();
                System.out.println(Thread.currentThread() + " enter out barrier");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        executorService.shutdown();
    }
}
