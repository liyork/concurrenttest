package com.wolf.concurrenttest.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Description:
 * <br/> Created on 2017/4/25 16:43
 *
 * @author 李超
 * @since 1.0.0
 */
public class ControlInvokeCount {

    private static AtomicInteger atomicInteger = new AtomicInteger();

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(6);
        for(int i = 0; i < 6; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    control();
                }
            });
        }


        executorService.shutdown();
    }

    public static void control(){
        System.out.println("111");
        int i = atomicInteger.addAndGet(1);
        if(i > 5) {
            throw new RuntimeException("xxxx");
        }
    }
}
