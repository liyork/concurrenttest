package com.wolf.concurrenttest.bfbczm.foundation;

/**
 * Description: 释放某个对象的锁资源，而不是都释放
 * Created on 2021/9/2 7:10 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ReleaseOtherLock {
    private static volatile Object resourceA = new Object();
    private static volatile Object resourceB = new Object();

    public static void main(String[] args) throws InterruptedException {
        // 线程a先获取a再获取b，释放a，然而b没释放
        Thread threadA = new Thread(() -> {
            try {
                synchronized (resourceA) {  // 获取resourceA共享资源的监视器锁
                    System.out.println("threadA get resourceA lock");

                    synchronized (resourceB) {
                        System.out.println("threadA get resourceB lock");

                        System.out.println("threadA release resourceA lock");
                        resourceA.wait();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        // 线程b先获取a，再获取b就获取不到了
        Thread threadB = new Thread(() -> {
            try {
                // 休眠，让线程a充分获取俩资源
                Thread.sleep(1000);

                synchronized (resourceA) {
                    System.out.println("threadB get resourceA lock");

                    System.out.println("threadB try get resource B lock..");
                    synchronized (resourceB) {
                        System.out.println("threadB get resourceB lock");

                        System.out.println("threadB release resourceA lock");
                        resourceA.wait();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        threadA.start();
        threadB.start();

        // 等待两个线程结束
        threadA.join();
        threadB.join();

        System.out.println("main over");
    }
}
