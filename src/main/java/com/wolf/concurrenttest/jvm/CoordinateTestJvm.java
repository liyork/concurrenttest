package com.wolf.concurrenttest.jvm;

import org.junit.Test;

import java.util.HashMap;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Description:
 * <br/> Created on 2017/5/25 11:07
 *
 * @author 李超
 * @since 1.0.0
 */
public class CoordinateTestJvm {

    @Test
    public void test() {
        HashMap<String, ThreadPoolExecutorObject> currentThreadPoolExecutor = new HashMap<>();
        int a = 5000000;
        //模拟内存溢出的场景

        for(int i = 0; i < a; i++) {
            ThreadPoolExecutor thread = new ThreadPoolExecutor(20, Integer.MAX_VALUE, 30l, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
            final int s = i;
            thread.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println(s);
                }
            });

            ThreadPoolExecutorObject threadPoolExecutorObject = new ThreadPoolExecutorObject();
            threadPoolExecutorObject.setPoolSize(thread.getPoolSize());
            threadPoolExecutorObject.setToString(thread.toString());

            currentThreadPoolExecutor.put(thread.toString(), threadPoolExecutorObject);

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
