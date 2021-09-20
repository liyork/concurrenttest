package com.wolf.concurrenttest.hcpta.group;

import java.util.concurrent.TimeUnit;

/**
 * Description: 中断线程组内所有active线程，递归
 * Created on 2021/9/19 11:07 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ThreadGroupInterrupt {
    public static void main(String[] args) throws InterruptedException {
        ThreadGroup group = new ThreadGroup("testGroup");
        new Thread(group, () -> {
            while (true) {
                try {
                    TimeUnit.MILLISECONDS.sleep(2);
                } catch (InterruptedException e) {
                    // received interrupt SIGNAL and clear quickly
                    break;
                }
            }
            System.out.println("t1 will exit");
        }, "t1").start();

        new Thread(group, () -> {
            while (true) {
                try {
                    TimeUnit.MILLISECONDS.sleep(1);
                } catch (InterruptedException e) {
                    // received interrupt SIGNAL and clear quickly
                    break;
                }
            }
            System.out.println("t2 will exit");
        }, "t2").start();

        // make sure all of above threads started
        TimeUnit.MILLISECONDS.sleep(2);

        group.interrupt();
    }
}
