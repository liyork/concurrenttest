package com.wolf.concurrenttest.lock.test;

import com.wolf.concurrenttest.lock.SimpleSpinLock;
import com.wolf.concurrenttest.common.TakeTimeUtils;

/**
 * Description:
 * <br/> Created on 2017/2/20 16:12
 *
 * @author 李超
 * @since 1.0.0
 */
public class SimpleSpinLockTest {

    public static void main(String[] args) {
        final SimpleSpinLock clhLock = new SimpleSpinLock();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                clhLock.lock();
                try {
                    TakeTimeUtils.simulateLongTimeOperation(5000000);
                    System.out.println(Thread.currentThread().getName() + " finish...");
                } finally {
                    clhLock.unlock();
                }
            }
        });
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                clhLock.lock();
                try {
                    TakeTimeUtils.simulateLongTimeOperation(5000000);
                    System.out.println(Thread.currentThread().getName() + " finish...");
                } finally {
                    clhLock.unlock();
                }
            }
        });
        Thread thread3 = new Thread(new Runnable() {
            @Override
            public void run() {
                clhLock.lock();
                try {
                    TakeTimeUtils.simulateLongTimeOperation(5000000);
                    System.out.println(Thread.currentThread().getName() + " finish...");
                } finally {
                    clhLock.unlock();
                }
            }
        });

        thread.start();
        thread2.start();
        thread3.start();
    }
}
