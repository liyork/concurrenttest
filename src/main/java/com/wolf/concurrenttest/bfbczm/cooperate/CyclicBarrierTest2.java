package com.wolf.concurrenttest.bfbczm.cooperate;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Description: 每个阶段任务的所有线程都完成后再执行下一个阶段的任务
 * Created on 2021/9/11 10:05 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class CyclicBarrierTest2 {
    private static CyclicBarrier cyclicBarrier = new CyclicBarrier(2);

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.submit(() -> {
            try {
                System.out.println(Thread.currentThread() + " step1");
                cyclicBarrier.await();

                System.out.println(Thread.currentThread() + " step2");
                cyclicBarrier.await();

                System.out.println(Thread.currentThread() + " step3");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        executorService.submit(() -> {
            try {
                System.out.println(Thread.currentThread() + " step1");
                cyclicBarrier.await();

                System.out.println(Thread.currentThread() + " step2");
                cyclicBarrier.await();

                System.out.println(Thread.currentThread() + " step3");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        executorService.shutdown();
    }
}
