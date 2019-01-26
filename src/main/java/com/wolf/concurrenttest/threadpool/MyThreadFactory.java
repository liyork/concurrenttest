package com.wolf.concurrenttest.threadpool;

import java.util.concurrent.*;

/**
 * <p/>
 * Custom thread factory
 * 可以监控、可以设定线程名称、属性、分组
 *
 * @author Brian Goetz and Tim Peierls
 */
public class MyThreadFactory implements ThreadFactory {
    private final String poolName;

    public MyThreadFactory(String poolName) {
        this.poolName = poolName;
    }

    public Thread newThread(Runnable runnable) {
        return new MyAppThread(runnable, poolName);
    }
}
