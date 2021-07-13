package com.wolf.concurrenttest.jcip.shutdown;

import java.io.PrintWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Description: 对外提供生命周期方法，自己内部管理
 * encapsulate an ExecutorService behind a higher-level service that provides its own lifecycle methods,
 * delegates to an ExecutorService instead of managing its own threads.
 * Created on 2021/7/3 10:29 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class LogServiceLifecycle {
    private final ExecutorService exec = Executors.newSingleThreadExecutor();
    private final PrintWriter writer = null;

    public void start() {
    }

    public void stop() throws InterruptedException {
        try {
            exec.shutdown();
            exec.awaitTermination(10, TimeUnit.SECONDS);
        } finally {
            writer.close();
        }
    }

    public void log(String msg) {
        try {
            exec.execute(new WriteTask(msg));
        } catch (RejectedExecutionException e) {
            e.printStackTrace();
        }
    }

    private class WriteTask implements Runnable {
        public WriteTask(String msg) {
        }

        @Override
        public void run() {

        }
    }
}
