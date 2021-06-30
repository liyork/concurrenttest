package com.wolf.concurrenttest.jcip;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * Description: using futureTask to preload data that is needed later
 * 不仅两者的数据可以交互，连异常也可以交互
 * Created on 2021/6/29 10:22 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class FutureTaskDemo {
    private final FutureTask<String> future = new FutureTask<>(() -> loadProductInfo());
    private final Thread thread = new Thread(future);

    // provides a start method to start the thread, since it is inadvisable to start a thread from a constructor or static initializer，因为this逃逸
    public void start() {
        thread.start();
    }

    // 需要结果时获取，若结果还未计算完成则要等待直到完成
    public String getProductInfo() throws DataLoadException, InterruptedException {
        try {
            return future.get();
        } catch (ExecutionException e) {// whatever the task code may throw, it is wrapped in an ExecutionException and rethrow from Future.get
            Throwable cause = e.getCause();
            if (cause instanceof DataLoadException) {
                throw (DataLoadException) cause;
            } else {
                throw FutureTaskDemo.launderThrowable(cause);
            }
        }
    }

    // coercing an Unchecked Throwable to a RuntimeException
    public  static  RuntimeException launderThrowable(Throwable t) {
        if (t instanceof RuntimeException) {
            return (RuntimeException) t;
        } else if (t instanceof Error) {
            throw (Error) t;
        } else {
            throw new IllegalStateException("Not unchecked", t);
        }
    }

    private String loadProductInfo() {
        return "xx";
    }

    private class DataLoadException extends Exception {
    }
}
