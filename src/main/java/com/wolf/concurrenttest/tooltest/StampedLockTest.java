package com.wolf.concurrenttest.tooltest;

import com.wolf.concurrenttest.common.TakeTimeUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.StampedLock;

/**
 * Description:乐观读写锁
 * 基于clh自旋锁实现,先获取，失败则自旋，再失败则进入队列等待,似乎是最快的?在任何场景(读多写少、读写一样、写多读少)？
 * <br/> Created on 24/03/2018 7:00 PM
 *
 * @author 李超
 * @since 1.0.0
 */
//todo 待研究
public class StampedLockTest {

    private double x, y;
    private static final StampedLock stampedLock = new StampedLock();

    void move(double deltaX, double deltaY) {
        long stamp = stampedLock.writeLock();
        try {
            x += deltaX;
            y += deltaY;
        } finally {
            stampedLock.unlockWrite(stamp);
        }
    }

    double distanceFromOrigin() {
        long stamp = stampedLock.tryOptimisticRead();//仅用这个判断不可行，尝试乐观读返回当时时间戳
        double currentX = x, currentY = y;
        if (!stampedLock.validate(stamp)) {//上步可能产生并发问题,这里判断下这个锁是否被其他写锁获取
            stamp = stampedLock.readLock();//升级
            try {
                currentX = x;
                currentY = y;
            } finally {
                stampedLock.unlockRead(stamp);
            }
        }
        if (currentX != currentY) {
            System.out.println("x:" + currentX + ",y=" + currentY);
        }

        return Math.sqrt(currentX * currentX + currentY * currentY);
    }

    public static void main(String[] args) throws InterruptedException {
//        testBase();
        testError();
    }

    private static void testBase() {
        StampedLockTest stampedLockTest = new StampedLockTest();
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        for (int i = 0; i < 5000; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    stampedLockTest.move(1, 1);
                }
            });
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    stampedLockTest.distanceFromOrigin();
                }
            });
        }

        executorService.shutdown();
    }

    //注意：若线程被中断，但是底层使用的U.park并未抛出异常，则继续循环，导致一直是runnable，占用cpu
    private static void testError() throws InterruptedException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                long stamp = stampedLock.writeLock();
                try {
                    Thread.sleep(100000000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    stampedLock.unlockWrite(stamp);
                }
            }
        }).start();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    long l = stampedLock.readLock();
                    System.out.println("read lock...");
                    TakeTimeUtils.simulateLongTimeOperation(500000);
                    stampedLock.unlockRead(l);
                }
            }
        },"readThread-->");
        thread.start();

        Thread.sleep(7000);
        thread.interrupt();
    }
}
