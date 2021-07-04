package com.wolf.concurrenttest.jcip.stop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Description: 演示UncaughtExceptionHandler
 * Created on 2021/7/4 8:44 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class UncaughtExceptionHandlerDemo {
    // 线程未caught异常而退出时调用
    static class UEHLogger implements Thread.UncaughtExceptionHandler {
        private final Logger logger = LoggerFactory.getLogger(UEHLogger.class);

        @Override
        public void uncaughtException(Thread t, Throwable e) {
            logger.warn("Thread terminated with exception: " + t.getName(), e);
        }
    }

    public static void main(String[] args) {
        Thread thread = new Thread();
        // 特定线程
        thread.setUncaughtExceptionHandler(new UEHLogger());
        // 所有线程
        Thread.setDefaultUncaughtExceptionHandler(new UEHLogger());

        // 为线程池的线程提供UncaughtExceptionHandler
        ExecutorService executorService = Executors.newCachedThreadPool(r -> {
            Thread thread1 = new Thread(r);
            thread1.setUncaughtExceptionHandler(new UEHLogger());
            return thread1;
        });

        // want to be notified when a task fails due to an exception so that you can take some task-specific recovery action,
        // either wrap the task with a Runnable or Callable that catches the exception or override the afterExecute hook in the ThreadPoolExecutor
        new ThreadPoolExecutor(1, 1, 1l, TimeUnit.SECONDS, null) {
            @Override
            protected void afterExecute(Runnable r, Throwable t) {
                System.out.println("run r occur exception, r:" + r + ", t:" + t);
                super.afterExecute(r, t);
            }
        };
    }
}
