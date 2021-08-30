package com.wolf.concurrenttest.taojcp.thread;

/**
 * Description: 线程状态测试
 * Created on 2021/8/26 1:52 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ThreadState {
    public static void main(String[] args) {
        new Thread(new TimeWaiting(), "TimeWaitingThread").start();
        new Thread(new Waiting(), "WaitingThread").start();
        // 用两个Blocked线程，一个获锁成功，一个被阻塞
        new Thread(new Blocked(), "BlockedThread-1").start();
        new Thread(new Blocked(), "BlockedThread-2").start();
    }

    // 该线程不断地进行睡眠
    static class TimeWaiting implements Runnable {
        @Override
        public void run() {
            while (true) {
                SleepUtils.second(100);  // TIMED_WAITING
            }
        }
    }

    // 该线程在Waiting.class实例上等待
    static class Waiting implements Runnable {

        @Override
        public void run() {
            while (true) {
                synchronized (Waiting.class) {
                    try {
                        Waiting.class.wait();  // WAITING
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    // 该线程在Blocked.class实例上加锁后，不会释放该锁
    static class Blocked implements Runnable {

        @Override
        public void run() {
            synchronized (Blocked.class) {  // 为获取锁线程状态：BLOCKED
                while (true) {
                    SleepUtils.second(100);  // TIMED_WAITING或
                }
            }
        }
    }
}
