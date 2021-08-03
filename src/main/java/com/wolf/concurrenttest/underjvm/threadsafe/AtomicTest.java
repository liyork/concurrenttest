package com.wolf.concurrenttest.underjvm.threadsafe;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Description: Atomic的原子自增运算演示
 * Created on 2021/8/3 9:20 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class AtomicTest {
    public static AtomicInteger race = new AtomicInteger(0);

    public static void increse() {
        race.incrementAndGet();
    }

    private static final int THREADS_COUNT = 20;

    public static void main(String[] args) {
        Thread[] threads = new Thread[THREADS_COUNT];
        for (int i = 0; i < THREADS_COUNT; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    increse();
                }
            });
            threads[i].start();
        }
        while (Thread.activeCount() > 1) {
            Thread.yield();
        }
        System.out.println(race);
    }
}
