package com.wolf.concurrenttest.jcip.gui;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.*;

/**
 * Description: implementing SwingUtilities Using an Executor
 * Created on 2021/7/6 1:40 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class MySwingUtilities {
    private static final ExecutorService exec = Executors.newSingleThreadExecutor();
    private static volatile Thread swingThread;

    private static class SwingThreadFactory implements ThreadFactory {
        @Override
        public Thread newThread(Runnable r) {
            swingThread = new Thread(r);
            return swingThread;
        }
    }

    public static boolean isEventDispatchThread() {
        return Thread.currentThread() == swingThread;
    }

    public static void invokeLater(Runnable task) {
        exec.execute(task);
    }

    public static void invokeAndWait(Runnable task) throws InvocationTargetException, InterruptedException {
        Future<?> f = exec.submit(task);
        try {
            f.get();
        } catch (ExecutionException e) {
            throw new InvocationTargetException(e);
        }
    }

}
