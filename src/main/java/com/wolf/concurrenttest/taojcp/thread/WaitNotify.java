package com.wolf.concurrenttest.taojcp.thread;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Description: 等待通知机制
 * Created on 2021/8/27 7:27 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class WaitNotify {
    static boolean flag = true;
    static Object lock = new Object();

    public static void main(String[] args) {
        Thread waitThread = new Thread(new Wait(), "WaitThread");
        waitThread.start();

        SleepUtils.second(1);

        Thread notifyThread = new Thread(new Notify(), "NotifyThread");
        notifyThread.start();
    }

    static class Wait implements Runnable {
        @Override
        public void run() {
            synchronized (lock) {  // 尝试拥有lock的Monitor
                while (flag) {
                    try {
                        System.out.println(Thread.currentThread() + " flag is true. wait @ " +
                                new SimpleDateFormat("HH:mm:ss").format(new Date()));
                        lock.wait();  // 睡眠，释放锁
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                // 条件满足
                System.out.println(Thread.currentThread() + " flag is false. running @ " +
                        new SimpleDateFormat("HH:mm:ss").format(new Date()));
            }
        }
    }

    static class Notify implements Runnable {
        @Override
        public void run() {
            synchronized (lock) {
                System.out.println(Thread.currentThread() + " hold lock. notify @ " +
                        new SimpleDateFormat("HH:mm:ss").format(new Date()));
                lock.notifyAll();  // 通知，但不会马上释放lock的锁，而是这里退出了synchronized才释放锁，然后WaitThread才能获取到锁
                flag = false;
                SleepUtils.second(5);
            }

            // 运行总是发生`hold lock again`，javap查看没有锁粗化，那应该就是抢占锁的时机赶上了
            // 再次加锁
            synchronized (lock) {
                System.out.println(Thread.currentThread() + " hold lock again. sleep @ " +
                        new SimpleDateFormat("HH:mm:ss").format(new Date()));
                SleepUtils.second(5);
            }
        }
    }
}