package com.wolf.concurrenttest.hcpta.cooperate;

import java.util.concurrent.TimeUnit;

/**
 * Description:
 * Created on 2021/9/18 9:10 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ClassMonitor {
    public static synchronized void method1() {
        System.out.println(Thread.currentThread().getName() + " enter to method1");
        try {
            TimeUnit.MILLISECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static synchronized void method2() {
        System.out.println(Thread.currentThread().getName() + " enter to method2");
        try {
            TimeUnit.MILLISECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void method3() {
        synchronized (ClassMonitor.class) {
            System.out.println(Thread.currentThread().getName() + " enter to method3");
            try {
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) {
        new Thread(ClassMonitor::method1, "t1").start();
        new Thread(ClassMonitor::method2, "t2").start();
    }
}
