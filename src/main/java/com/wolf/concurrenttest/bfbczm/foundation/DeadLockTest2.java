package com.wolf.concurrenttest.bfbczm.foundation;

/**
 * Description: 顺序获取锁，减少死锁风险
 * Created on 2021/9/2 10:05 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class DeadLockTest2 {
    private static Object resourceA = new Object();
    private static Object resourceB = new Object();

    public static void main(String[] args) {
        Thread threadA = new Thread(() -> {
            synchronized (resourceA) {
                System.out.println(Thread.currentThread() + " get ResourceA");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println(Thread.currentThread() + " waiting get sourceB");
                synchronized (resourceB) {
                    System.out.println(Thread.currentThread() + " get resourceB");
                }
            }
        });

        Thread threadB = new Thread(() -> {
            synchronized (resourceA) {
                System.out.println(Thread.currentThread() + " get resourceA");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println(Thread.currentThread() + " waiting get resourceB");
                synchronized (resourceB) {
                    System.out.println(Thread.currentThread() + " get resourceB");
                }
            }
        });

        threadA.start();
        threadB.start();
    }
}
