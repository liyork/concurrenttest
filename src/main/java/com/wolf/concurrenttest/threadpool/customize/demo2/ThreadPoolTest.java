package com.wolf.concurrenttest.threadpool.customize.demo2;

import java.util.concurrent.TimeUnit;

/**
 * Description:
 * <br/> Created on 2019-01-27
 *
 * @author 李超
 * @since 1.0.0
 */
public class ThreadPoolTest {

    public static void main(String[] args) throws InterruptedException {

        MyThreadPool threadPool = start();
//        testBase(threadPool);
        testDestroy(threadPool);
    }

    private static void testBase(MyThreadPool threadPool) throws InterruptedException {

        while (true) {
            //数量最后回归到core
            System.out.println("getActiveWorkerCount:" + threadPool.getActiveWorkerCount());
            System.out.println("getTaskQueueSize:" + threadPool.getTaskQueueSize());
//            System.out.println("getCoreSize:"+threadPool.getCoreSize());
//            System.out.println("getMaxSize:"+threadPool.getMaxSize());

            TimeUnit.SECONDS.sleep(5);
            System.out.println();
        }
    }

    private static MyThreadPool start() {
        MyThreadPool threadPool = new MyThreadPool(2, 6, 4, 1000);

        for (int i = 0; i < 20; i++) {
            threadPool.execute(() -> {
                try {
                    TimeUnit.SECONDS.sleep(10);
                    System.out.println(Thread.currentThread().getName() + " is running and done.");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

        System.out.printf("init:%d,core:%d,max:%d", 2, 4, 6);
        System.out.println();
        return threadPool;
    }

    private static void testDestroy(MyThreadPool threadPool) throws InterruptedException {

        TimeUnit.SECONDS.sleep(12);
        threadPool.shutdown();
        synchronized (ThreadPoolTest.class) {
            ThreadPoolTest.class.wait();
        }
    }
}
