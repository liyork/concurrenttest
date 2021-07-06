package com.wolf.concurrenttest.jcip.threadpool;


import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Description: Custom thread factory
 * 可以监控、可以设定线程名称、属性、分组
 * 对于安全，可以使用PrivilegedThreadFactory
 * Created on 2021/7/5 6:28 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class MyThreadFactory implements ThreadFactory {
    private static final AtomicInteger GROUP_COUNTER = new AtomicInteger(1);

    private static final ThreadGroup GROUP = new ThreadGroup("MyThreadPool-" + GROUP_COUNTER.getAndIncrement());

    private static final AtomicInteger COUNTER = new AtomicInteger();

    private final String poolNamePrefix;

    public MyThreadFactory(String poolNamePrefix) {
        this.poolNamePrefix = poolNamePrefix;
    }

    @Override
    public Thread newThread(Runnable runnable) {
        //return new MyAppThread(runnable, poolNamePrefix);
        return new MyAppThread(GROUP, runnable, poolNamePrefix + COUNTER.getAndIncrement());
    }
}
