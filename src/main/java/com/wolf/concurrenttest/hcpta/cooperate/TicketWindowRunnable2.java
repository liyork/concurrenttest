package com.wolf.concurrenttest.hcpta.cooperate;

/**
 * Description:
 * Created on 2021/9/16 6:51 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class TicketWindowRunnable2 implements Runnable {

    private static final int MAX = 50;

    private int index = 1;

    private final static Object MUTEX = new Object();

    @Override
    public void run() {
        synchronized (MUTEX) {
            while (index <= MAX) {
                System.out.println(Thread.currentThread() + " 的号码是：" + (index++));
            }
        }
    }

    public static void main(String[] args) {
        final TicketWindowRunnable2 task = new TicketWindowRunnable2();
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
