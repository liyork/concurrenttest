package com.wolf.concurrenttest.taojcp.thread;

import java.util.concurrent.TimeUnit;

/**
 * Description: 中断敏感性测试
 * Created on 2021/8/26 10:14 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class InterruptedTest {
    public static void main(String[] args) {
        Thread sleepThread = new Thread(new SleepRunner(), "SleepThread");
        sleepThread.setDaemon(true);

        Thread busyThread = new Thread(new BusyRunner(), "BusyThread");
        busyThread.setDaemon(true);

        sleepThread.start();
        busyThread.start();

        SleepUtils.second(3);

        sleepThread.interrupt();
        busyThread.interrupt();

        SleepUtils.second(5);

        System.out.println("SleepThread is alive: " + sleepThread.isAlive() + ", interrupted is: " + sleepThread.isInterrupted());
        System.out.println("BusyThread is alive: " + busyThread.isAlive() + ", interrupted is: " + busyThread.isInterrupted());

        // 防止两个线程立即退出
        SleepUtils.second(2);
    }

    // 抛出InterruptedException异常，中断标识位被清除
    static class SleepRunner implements Runnable {
        @Override
        public void run() {
            while (true) {
                // 这里会catch，然后while还会继续下次等待
                //SleepUtils.second(10);
                // 线程只要退出了run，那么就不是isAlive=true了
                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    static class BusyRunner implements Runnable {
        @Override
        public void run() {
            while (true) {
            }
        }
    }
}
