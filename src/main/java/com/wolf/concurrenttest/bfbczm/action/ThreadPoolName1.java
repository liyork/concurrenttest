package com.wolf.concurrenttest.bfbczm.action;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Description: 线程池设定名称，便于排查问题
 * Created on 2021/9/14 6:34 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ThreadPoolName1 {
    static ThreadPoolExecutor executorOne = new ThreadPoolExecutor(
            5, 5, 1, TimeUnit.MINUTES,
            new LinkedBlockingQueue<>(), new NamedThreadFactory("ASYN-ACCEPT-POOL"));
    static ThreadPoolExecutor executorTwo = new ThreadPoolExecutor(
            5, 5, 1, TimeUnit.MINUTES,
            new LinkedBlockingQueue<>(), new NamedThreadFactory("ASYN-PROCESS-POOL"));

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

// 命名线程工厂
class NamedThreadFactory implements ThreadFactory {
    private static final AtomicInteger poolNumber = new AtomicInteger(1);
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;

    NamedThreadFactory(String name) {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        if (null == name || name.isEmpty()) {
            name = "pool";
        }
        namePrefix = name + "-" + poolNumber.getAndIncrement() + "-thread-";
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
        if (t.isDaemon()) {
            t.setDaemon(false);
        }
        if (t.getPriority() != Thread.NORM_PRIORITY) {
            t.setPriority(Thread.NORM_PRIORITY);
        }
        return t;
    }
}