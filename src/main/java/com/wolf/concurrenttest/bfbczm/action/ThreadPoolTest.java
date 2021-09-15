package com.wolf.concurrenttest.bfbczm.action;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Description: 线程池+tl，内存泄露问题
 * Created on 2021/9/14 10:03 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ThreadPoolTest {
    static class LocalVariable {
        private Long[] a = new Long[1024 * 1024];
    }

    final static ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(5, 5, 1, TimeUnit.MINUTES,
            new LinkedBlockingQueue<>());

    final static ThreadLocal<LocalVariable> localVariable = new ThreadLocal<>();

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 50; i++) {
            poolExecutor.execute(() -> {
                localVariable.set(new LocalVariable());
                System.out.println("use local variable");
                // 一定要记得释放
                //localVariable.remove();
            });
            TimeUnit.SECONDS.sleep(1);
        }
        System.out.println("pool execute over");
    }
}
