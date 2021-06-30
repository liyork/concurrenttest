package com.wolf.concurrenttest.jcip;

import java.util.concurrent.CountDownLatch;

/**
 * Description: 多任务并发执行，用CountDownLatch控制进度
 * 开始时每个worker线程在startGate上等待，保证都等主线程开始。主线程等待在endGate，所有线程执行countDown
 * <p>
 * Created on 2021/6/29 10:01 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class CountDownLatchDemo {
    public long timeTasks(int nThreads, final Runnable task) throws InterruptedException {
        final CountDownLatch startGate = new CountDownLatch(1);
        final CountDownLatch endGate = new CountDownLatch(nThreads);

        for (int i = 0; i < nThreads; i++) {
            new Thread(() -> {
                try {
                    startGate.await();  // 等待main的信号再开始
                    try {
                        task.run();
                    } finally {
                        endGate.countDown();
                    }
                } catch (InterruptedException ignored) {
                }
            }).start();
        }

        long start = System.nanoTime();
        startGate.countDown();
        endGate.await();  // 等待大家结束
        long end = System.nanoTime();
        return end - start;
    }
}
