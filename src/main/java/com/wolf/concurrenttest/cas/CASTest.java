package com.wolf.concurrenttest.cas;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Description:CAS一定要volatile变量配合
 * <br/> Created on 2017/7/3 16:36
 *
 * @author 李超
 * @since 1.0.0
 */
public class CASTest {

    AtomicInteger atomicInteger = new AtomicInteger(0);

    public static void main(String[] args) {

        final CASTest casTest = new CASTest();

        ExecutorService executorService = Executors.newFixedThreadPool(500);
        for(int i = 0; i < 500; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    casTest.increaseHasTop();
                }
            });
        }

        executorService.shutdown();
    }

    //需求：每个人都+1，但是不能超过100，并发执行，不用锁
    public void increaseHasTop() {

        Integer s = atomicInteger.get();

        if(s < 100) {
            atomicInteger.compareAndSet(s, s + 1);
        }

        if(atomicInteger.get() > 100) {
            System.out.println(atomicInteger.get());
        }
    }

}