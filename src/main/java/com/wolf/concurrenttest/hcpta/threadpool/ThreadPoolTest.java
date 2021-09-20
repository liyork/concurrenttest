package com.wolf.concurrenttest.hcpta.threadpool;

import java.util.concurrent.TimeUnit;

/**
 * Description: 测试线程池的任务提交、线程池线程数的动态扩展，以及线程池的销毁
 * 看到active count停留在core size的位置
 * Created on 2021/9/20 9:51 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ThreadPoolTest {
    public static void main(String[] args) throws InterruptedException {
        final ThreadPool threadPool = new BasicThreadPool(2, 6, 4, 100);

        for (int i = 0; i < 20; i++) {
            threadPool.execute(() -> {
                try {
                    TimeUnit.SECONDS.sleep(10);
                    System.out.println(Thread.currentThread().getName() + " is running and done");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

        //monitor(threadPool);
        shutdown(threadPool);
    }

    private static void shutdown(ThreadPool threadPool) throws InterruptedException {
        TimeUnit.SECONDS.sleep(12);
        threadPool.shutdown();
        System.out.println("thread pool is shutdown finish");
        TimeUnit.SECONDS.sleep(5);
    }

    private static void monitor(ThreadPool threadPool) throws InterruptedException {
        // 输出线程池信息
        for (; ; ) {
            // 主要观察这俩
            System.out.println("getActiveCount: " + threadPool.getActiveCount());
            System.out.println("getQueueSize: " + threadPool.getQueueSize());
            // 固定值
            System.out.println("getCoreSize: " + threadPool.getCoreSize());
            System.out.println("getMaxSize: " + threadPool.getMaxSize());
            System.out.println("=======================");
            TimeUnit.SECONDS.sleep(3);
        }
    }
}
