package com.wolf.concurrenttest.jcip.threadpool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Description: Custom thread base class
 * Created on 2021/7/5 6:29 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class MyAppThread extends Thread {
    public static final String DEFAULT_NAME = "MyAppThread";
    private static volatile boolean debugLifecycle = false;
    private static final AtomicInteger created = new AtomicInteger();
    private static final AtomicInteger alive = new AtomicInteger();
    private static final Logger logger = LoggerFactory.getLogger(MyAppThread.class);

    public MyAppThread(Runnable r) {
        this(r, DEFAULT_NAME);
    }

    public MyAppThread(ThreadGroup group, Runnable target, String name) {
        super(group, target, name);
    }

    //重写目的：修改线程名称，添加意外异常捕获机制
    public MyAppThread(Runnable runnable, String name) {
        super(runnable, name + "_" + created.incrementAndGet());
        setUncaughtExceptionHandler((t, e) -> logger.warn("UNCAUGHT in thread " + t.getName(), e));
    }

    @Override
    public void run() {
        // copy debug flag to ensure consistent value throughout
        boolean debug = debugLifecycle;
        if (debug) logger.warn("Created " + getName());
        try {
            alive.incrementAndGet();
            super.run();
        } finally {
            alive.decrementAndGet();
            if (debug) logger.warn("Exiting " + getName());
        }
    }

    public static int getThreadsCreated() {
        return created.get();
    }

    public static int getThreadsAlive() {
        return alive.get();
    }

    public static boolean getDebug() {
        return debugLifecycle;
    }

    public static void setDebug(boolean b) {
        debugLifecycle = b;
    }
}
