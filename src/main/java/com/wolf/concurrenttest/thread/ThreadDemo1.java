package com.wolf.concurrenttest.thread;

import java.util.concurrent.CountDownLatch;

/**
 * Description: 4个线程，其中两个线程每次对j增加1，另外两个线程对j每次减少1
 * 对j增减的时候没有考虑顺序问题。
 *
 * @author 李超
 * @date 2020/01/31
 */
public class ThreadDemo1 {

    private static int j;
    private static CountDownLatch countDownLatch = new CountDownLatch(2);

    public static void main(String args[]) throws InterruptedException {
        ThreadDemo1 tt = new ThreadDemo1();
        Inc inc = tt.new Inc();
        Dec dec = tt.new Dec();
        for (int i = 0; i < 2; i++) {
            Thread t = new Thread(inc);
            t.start();
            t = new Thread(dec);
            t.start();
        }

        countDownLatch.await();
        System.out.println("j:" + j);
    }

    private synchronized void inc() {
        j++;
        //System.out.println(Thread.currentThread().getName() + "-inc:" + j);
    }

    private synchronized void dec() {
        j--;
        //System.out.println(Thread.currentThread().getName() + "-dec:" + j);
    }

    class Inc implements Runnable {
        public void run() {
            for (int i = 0; i < 100; i++) {
                inc();
            }
            countDownLatch.countDown();
        }
    }

    class Dec implements Runnable {
        public void run() {
            for (int i = 0; i < 100; i++) {
                dec();
            }
            countDownLatch.countDown();
        }
    }
}
