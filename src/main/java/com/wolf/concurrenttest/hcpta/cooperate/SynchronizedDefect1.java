package com.wolf.concurrenttest.hcpta.cooperate;

import java.util.concurrent.TimeUnit;

/**
 * Description: 阻塞无法中断
 * synchronized无法像sleep/wait能捕获到中断信号
 * Created on 2021/9/19 7:07 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class SynchronizedDefect1 {
    public synchronized void syncMethod() {
        try {
            TimeUnit.HOURS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        SynchronizedDefect1 defect = new SynchronizedDefect1();
        Thread t1 = new Thread(defect::syncMethod, "t1");
        t1.start();
        // make sure the t1 started
        TimeUnit.MILLISECONDS.sleep(2);

        Thread t2 = new Thread(defect::syncMethod, "t2");
        t2.start();
        // make sure the t2 started
        TimeUnit.MILLISECONDS.sleep(2);

        t2.interrupt();
        System.out.println(t2.isInterrupted());
        System.out.println(t2.getState());
    }
}
