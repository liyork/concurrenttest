package com.wolf.concurrenttest.threadpool;

import java.util.concurrent.*;

/**
 * <p> Description: ScheduledExecutorService 优于timer，线程池比多个timer省资源
 * 内部使用DelayedWorkQueue，组合了PriorityQueue功能
 * 将runnable包装成ScheduledFutureTask，然后从DelayedWorkQueue中取执行完后则+period再放入队列
 * <p>
 * 由于DelayedWorkQueue是无长度队列则超时时间没用。
 *
 * 超时放弃机制：workQueue.poll(keepAliveTime, TimeUnit.NANOSECONDS)
 *
 * <p/>
 * Date: 2015/11/13
 * Time: 13:57
 *
 * @author 李超
 * @version 1.0
 * @since 1.0
 */
public class ScheduledExecutorTest {

    public static void main(String[] args) {
//        testBase();
//		testDiff();
//        testException();
        testTimeout0();
    }

    private static void testBase() {
        //corePoolSize也仅仅是提供多个线程执行给定任务
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(10);
        for (int i = 0; i < 10; i++) {
            executorService.scheduleWithFixedDelay(new Cat(), 1, 20, TimeUnit.SECONDS);
        }
    }

    private static void testDiff() {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);
        for (int i = 0; i < 1; i++) {
            //如果上一个任务晚了，就在上一个任务结束后再延迟2秒执行(即等待5s后再等2s，那么间隔就是7s)
            executorService.scheduleWithFixedDelay(new Cat(), 10, 20, TimeUnit.SECONDS);
            //如果上一个任务晚了，就在上一个任务结束马上执行(即等待5秒后马上执行，那么间隔就是5s)
            //executorService.scheduleAtFixedRate(new Cat(), 1, 2, TimeUnit.SECONDS);
        }
    }

    //If any execution of the task encounters an exception, subsequent executions are suppressed.Otherwise,
    // the task will only terminate via cancellation or termination of the executor.
    private static void testException() {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(10);

        final int[] i = {1};
        executorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {

                try {
                    System.out.println("haha+i:" + i[0]++);
                    if (i[0] == 5) {
                        throw new RuntimeException("xxx");
                    }
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }
        }, 2, 3, TimeUnit.SECONDS);
    }

    private static void testTimeout0() {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 5, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1));
        for (int i = 0; i < 10; i++) {
            threadPoolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println(Thread.currentThread().getName() + " is running");
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    static class Cat implements Runnable{

        @Override
        public void run() {
            try {
                System.out.println(Thread.currentThread().getName()+" begin run ... time:"+System.currentTimeMillis());
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                System.out.println(Thread.currentThread().getName()+" catch run ..."+System.currentTimeMillis());
            }
        }
    }
}
