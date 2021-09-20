package com.wolf.concurrenttest.hcpta.group;

import java.util.concurrent.TimeUnit;

/**
 * Description:
 * Created on 2021/9/19 11:17 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ThreadGropuDaemon {
    public static void main(String[] args) throws InterruptedException {
        ThreadGroup group1 = new ThreadGroup("group1");
        new Thread(group1, () -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "group1-thread1").start();

        ThreadGroup group2 = new ThreadGroup("group2");
        new Thread(group2, () -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "group2-thread1").start();

        group2.setDaemon(true);

        TimeUnit.SECONDS.sleep(3);
        System.out.println(group1.isDestroyed());
        // 无active线程时，该group自动被destroy
        System.out.println(group2.isDestroyed());
    }
}
