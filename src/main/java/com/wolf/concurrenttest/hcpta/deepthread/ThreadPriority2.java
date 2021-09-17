package com.wolf.concurrenttest.hcpta.deepthread;

/**
 * Description: 优先级继承性
 * Created on 2021/9/17 7:09 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ThreadPriority2 {
    public static void main(String[] args) {
        Thread t1 = new Thread();
        System.out.println("t1 priority " + t1.getPriority());

        Thread t2 = new Thread(() -> {
            Thread t3 = new Thread();
            System.out.println("t3 priority " + t3.getPriority());
        });

        t2.setPriority(6);
        t2.start();
        System.out.println("t2 priority " + t2.getPriority());

    }
}
