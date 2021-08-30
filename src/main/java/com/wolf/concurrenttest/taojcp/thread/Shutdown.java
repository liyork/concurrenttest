package com.wolf.concurrenttest.taojcp.thread;

/**
 * Description: 测试终止线程的几种方式
 * Created on 2021/8/27 6:28 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class Shutdown {
    public static void main(String[] args) {
        Runner one = new Runner();
        Thread countThread1 = new Thread(one, "CountThread1");
        countThread1.start();

        SleepUtils.second(1);
        countThread1.interrupt();  // 中断，使CountThread1能感知到该停止

        Runner two = new Runner();
        Thread countThread2 = new Thread(two, "CountThread2");
        countThread2.start();

        SleepUtils.second(1);
        two.cancel();  // 取消，使CountThread1能感知到该停止
    }

    static class Runner implements Runnable {
        private long i;
        private volatile boolean on = true;

        @Override
        public void run() {
            while (on && !Thread.currentThread().isInterrupted()) {
                i++;
            }
            System.out.println("Count i = " + i);
        }

        public void cancel() {
            on = false;
        }
    }
}
