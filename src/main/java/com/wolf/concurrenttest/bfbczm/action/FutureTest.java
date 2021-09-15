package com.wolf.concurrenttest.bfbczm.action;

import java.util.concurrent.*;

/**
 * Description: 演示丢弃策略会导致future.get一直等待
 * Created on 2021/9/14 9:04 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class FutureTest {
    // 单线程，队列元素为1
    private final static ThreadPoolExecutor executorService = new ThreadPoolExecutor(1, 1, 1L, TimeUnit.MINUTES,
            new ArrayBlockingQueue<>(1), new ThreadPoolExecutor.DiscardPolicy());

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // 线程执行任务
        Future<?> futureOne = executorService.submit(() -> {
            System.out.println("start runable one");
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        // 入队列
        Future<?> futureTwo = executorService.submit(() -> {
            System.out.println("start runable two");
        });

        // 拒绝丢弃
        // 若用AbortPolicy则submit直接抛出异常
        Future<?> futureThree = null;
        try {
            futureThree = executorService.submit(() -> {
                System.out.println("start runable three");
            });
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }

        System.out.println("task one " + futureOne.get());
        System.out.println("task two " + futureTwo.get());
        // 一直阻塞
        System.out.println("task three " + futureThree == null ? null : futureThree.get());

        executorService.shutdown();
    }
}
