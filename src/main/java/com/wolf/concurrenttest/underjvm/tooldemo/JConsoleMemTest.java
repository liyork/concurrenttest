package com.wolf.concurrenttest.underjvm.tooldemo;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Description: JConsole监视代码
 * -Xms100m -Xmx100m -XX:+UseSerialGC
 * Created on 2021/7/21 6:16 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class JConsoleMemTest {
    // 内存占位符对象：一个OOMObject大约占64K
    static class OOMObject {
        public byte[] placeholder = new byte[64 * 1024];
    }

    // 50ms速度填充堆64K
    private static void fillHeap(int num) throws InterruptedException {
        ArrayList<OOMObject> list = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            // 稍微延时，令监视曲线的变化更加明显
            TimeUnit.MILLISECONDS.sleep(50);
            list.add(new OOMObject());
        }
        System.gc();
    }

    // 线程等待演示
    public static void createBusyThread() {
        new Thread(() -> {
            while (true) {
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "testBusyThread").start();
    }

    // 线程锁等待演示
    public static void createLockThread(final Object lock) {
        new Thread(() -> {
            synchronized (lock) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "testLockThread").start();
    }

    // 死锁等待演示
    static class SynAddRunnable implements Runnable {
        int a, b;

        public SynAddRunnable(int a, int b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public void run() {
            synchronized (Integer.valueOf(a)) {
                synchronized (Integer.valueOf(b)) {
                    System.out.println(a + b);
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        fillHeap(10000000);

        //createBusyThread();
        //Object obj = new Object();
        //createLockThread(obj);

        //for (int i = 0; i < 100; i++) {
        //    new Thread(new SynAddRunnable(1, 2)).start();
        //    new Thread(new SynAddRunnable(2, 1)).start();
        //}
    }
}
