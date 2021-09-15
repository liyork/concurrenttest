package com.wolf.concurrenttest.bfbczm.action;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Description: 线程池未设定名称
 * Created on 2021/9/14 6:34 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ThreadPoolName {
    static ThreadPoolExecutor executorOne = new ThreadPoolExecutor(
            5, 5, 1, TimeUnit.MINUTES, new LinkedBlockingQueue<>());
    static ThreadPoolExecutor executorTwo = new ThreadPoolExecutor(
            5, 5, 1, TimeUnit.MINUTES, new LinkedBlockingQueue<>());

    public static void main(String[] args) {
        executorOne.execute(() -> {
            System.out.println("接收用户连接线程");
            throw new NullPointerException();
        });

        executorTwo.execute(() -> {
            System.out.println("具体处理业务请求线程");
        });

        executorOne.shutdown();
        executorTwo.shutdown();
    }
}
