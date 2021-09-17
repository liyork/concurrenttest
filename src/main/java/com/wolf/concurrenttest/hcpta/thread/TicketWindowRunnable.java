package com.wolf.concurrenttest.hcpta.thread;

import java.util.concurrent.TimeUnit;

/**
 * Description:
 * Created on 2021/9/16 6:51 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class TicketWindowRunnable implements Runnable {

    private static final int MAX = 50;

    private int index = 1;

    @Override
    public void run() {
        while (index <= MAX) {
            System.out.println(Thread.currentThread() + " 的号码是：" + (index++));
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        final TicketWindowRunnable task = new TicketWindowRunnable();
        Thread t1 = new Thread(task, "1号窗口");
        Thread t2 = new Thread(task, "2号窗口");
        Thread t3 = new Thread(task, "3号窗口");
        Thread t4 = new Thread(task, "4号窗口");

        t1.start();
        t2.start();
        t3.start();
        t4.start();
    }
}
