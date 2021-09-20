package com.wolf.concurrenttest.hcpta.cooperate;

import akka.dispatch.forkjoin.ThreadLocalRandom;

import java.util.concurrent.TimeUnit;

/**
 * Description: 可被中断测试
 * Created on 2021/9/19 8:14 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class BooleanLockTest1 {
    private final Lock lock = new BooleanLock();

    public void syncMethod() throws InterruptedException {
        lock.lock();
        try {
            int randomInt = ThreadLocalRandom.current().nextInt(10);
            System.out.println(Thread.currentThread() + " get the lock");
            TimeUnit.SECONDS.sleep(randomInt);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {// 用try-finally保证锁释放
            lock.unlock();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        BooleanLockTest blt = new BooleanLockTest();
        new Thread(() -> {
            try {
                blt.syncMethod();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t1").start();

        TimeUnit.MILLISECONDS.sleep(2);

        Thread t2 = new Thread(() -> {
            try {
                blt.syncMethod();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t2");
        t2.start();

        TimeUnit.MILLISECONDS.sleep(10);
        t2.interrupt();
    }
}
