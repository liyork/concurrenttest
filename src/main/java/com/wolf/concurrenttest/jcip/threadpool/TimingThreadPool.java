package com.wolf.concurrenttest.jcip.threadpool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Thread pool extended with logging and timing
 * because execution hooks are called in the thread that executes the tasks, a value placed in a
 * ThreadLocal by beforeExecute can be retrieved by afterExecute
 */
public class TimingThreadPool extends ThreadPoolExecutor {
    private final Logger log = LoggerFactory.getLogger(TimingThreadPool.class);
    private final ThreadLocal<Long> startTime = new ThreadLocal<>();
    private final AtomicLong numTasks = new AtomicLong();
    private final AtomicLong totalTime = new AtomicLong();

    public TimingThreadPool(int corePoolSize, int maxPoolSize) {
        super(corePoolSize, maxPoolSize, 10L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(128));
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        log.debug(String.format("Thread %s: start %s", t, r));
        startTime.set(System.nanoTime());
    }

    // is called whether the task cmopletes by returning normally from run or by throwing an Exception
    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        try {
            long endTime = System.nanoTime();
            long taskTime = endTime - startTime.get();
            numTasks.incrementAndGet();
            totalTime.addAndGet(taskTime);
            log.debug(String.format("Thread %s: end %s, time=%dns", t, r, taskTime));
        } finally {
            super.afterExecute(r, t);
        }
    }

    // is called when the thread pool completes the shutdown process,
    // after all tasks have finished and all worker threads have shut down.
    @Override
    protected void terminated() {
        try {
            log.info(String.format("Terminated: avg time=%dns", totalTime.get() / numTasks.get()));
        } finally {
            super.terminated();
        }
    }

}
