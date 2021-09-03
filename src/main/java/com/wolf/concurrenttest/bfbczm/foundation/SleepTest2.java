package com.wolf.concurrenttest.bfbczm.foundation;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Description: 线程在睡眠时拥有的监视器资源不会被释放
 * Created on 2021/9/2 1:27 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class SleepTest2 {
    private static final Lock lock = new ReentrantLock();

    public static void main(String[] args) {
        Thread threadA = new Thread(() -> {
            lock.lock();
            try {
                System.out.println("child threadA is in sleep");
                TimeUnit.SECONDS.sleep(5);  // 5s内仍持有锁
                System.out.println("child threadA is in awaked");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        });

        Thread threadB = new Thread(() -> {
            lock.lock();
            try {
                System.out.println("child threadB is in sleep");
                TimeUnit.SECONDS.sleep(5);
                System.out.println("child threadB is in awaked");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        });

        threadA.start();
        threadB.start();
    }
}
