package com.wolf.concurrenttest.threadpool;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p/>
 * Custom thread factory
 * 可以监控、可以设定线程名称、属性、分组
 *
 * @author Brian Goetz and Tim Peierls
 */
public class MyThreadFactory implements ThreadFactory {

    private static final AtomicInteger GROUP_COUNTER = new AtomicInteger(1);

    private static final ThreadGroup GROUP = new ThreadGroup("MyThreadPool-" + GROUP_COUNTER.getAndIncrement());

    private static final AtomicInteger COUNTER = new AtomicInteger();

    private final String poolNamePrefix;

    public MyThreadFactory(String poolNamePrefix) {
        this.poolNamePrefix = poolNamePrefix;
    }

    public Thread newThread(Runnable runnable) {

        return new Thread(GROUP, runnable, poolNamePrefix + COUNTER.getAndIncrement());
    }
}
