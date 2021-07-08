package com.wolf.concurrenttest.jcip.deadlock;

/**
 * Description: 交叉锁引起的死锁，线程都进入BLOCKED状态,CPU占用不高。
 * 与LeftRightDeadlock类似
 * <p>
 * 但是hashmap等死锁，线程都处于RUNNABLE状态，CPU使用率很高。
 * 死循环也容易占用CPU一直不放。
 * <br/> Created on 2019-01-27
 *
 * @author 李超
 * @since 1.0.0
 */
public class SimpleDeadLock {

    //jstack -l pid
    public static void test() throws InterruptedException {
        final Object aLock = new Object();
        final Object bLock = new Object();

        new Thread(() -> {
            synchronized (aLock) {
                System.out.println("aLockBLock in aLock");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (bLock) {
                    System.out.println("aLockBLock in bLock...");
                    try {
                        Thread.sleep(100000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, "aLock--bLock").start();


        new Thread(() -> {
            synchronized (bLock) {
                System.out.println("bLockALock in bLock");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (aLock) {
                    System.out.println("bLockALock in aLock...");
                    try {
                        Thread.sleep(100000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, "bLock--aLock").start();
    }


    public static void main(String[] args) throws InterruptedException {
        SimpleDeadLock.test();
    }
}
