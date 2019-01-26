package com.wolf.concurrenttest.lock.test;

import com.alibaba.fastjson.JSON;
import com.wolf.concurrenttest.lock.CLHLock;
import com.wolf.concurrenttest.common.TakeTimeUtils;

/**
 * Description:
 * <br/> Created on 2017/2/20 16:12
 *
 * @author 李超
 * @since 1.0.0
 */
public class CLHLockTest {

    public static void main(String[] args) {
        final CLHLock clhLock = new CLHLock();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                clhLock.lock();
                try {
                    TakeTimeUtils.simulateLongTimeOperation(5000000);
                    System.out.println(Thread.currentThread().getName() + " finish...");
                } finally {
                    clhLock.unlock();
                    System.out.println(Thread.currentThread().getName() +" mynode:"+ JSON.toJSONString(clhLock.getCurrentNodeThreadLocal().get())+" prenode:"+ JSON.toJSONString(clhLock.getPreNodeThreadLocal().get())+" tail:"+ JSON.toJSONString(clhLock.getTail()));
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
                    System.out.println(Thread.currentThread().getName() +" mynode:"+ JSON.toJSONString(clhLock.getCurrentNodeThreadLocal().get())+" prenode:"+ JSON.toJSONString(clhLock.getPreNodeThreadLocal().get())+" tail:"+ JSON.toJSONString(clhLock.getTail()));
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
                    System.out.println(Thread.currentThread().getName() +" mynode:"+ JSON.toJSONString(clhLock.getCurrentNodeThreadLocal().get())+" prenode:"+ JSON.toJSONString(clhLock.getPreNodeThreadLocal().get())+" tail:"+ JSON.toJSONString(clhLock.getTail()));
                }
            }
        });

        thread.start();
        thread2.start();
        thread3.start();
    }
}
