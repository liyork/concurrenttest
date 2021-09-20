package com.wolf.concurrenttest.hcpta.cooperate;

import java.util.concurrent.TimeUnit;

/**
 * Description: 互斥性
 * 只有一个线程时TIMED_WAITING(sleeping)，其他都是BLOCKED
 * jstack pid
 * javap -c Mutex
 * Created on 2021/9/18 7:26 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class Mutex {
    private final static Object MUTEX = new Object();

    public void accessResource() {
        synchronized (MUTEX) {
            try {
                TimeUnit.MINUTES.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        final Mutex mutex = new Mutex();
        for (int i = 0; i < 5; i++) {
            new Thread(mutex::accessResource).start();
        }
    }
}
