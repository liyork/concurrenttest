package com.wolf.concurrenttest.hcpta.deepthread;

/**
 * Description:
 * Created on 2021/9/17 6:58 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ThreadPriority {
    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            while (true) {
                System.out.println("t1");
            }
        });
        t1.setPriority(3);

        Thread t2 = new Thread(() -> {
            while (true) {
                System.out.println("t2");
            }
        });
        t2.setPriority(10);

        t1.start();
        t2.start();
    }
}
