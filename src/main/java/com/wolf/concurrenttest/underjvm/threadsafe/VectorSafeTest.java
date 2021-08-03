package com.wolf.concurrenttest.underjvm.threadsafe;

import java.util.Vector;
import java.util.concurrent.TimeUnit;

/**
 * Description: 对Vector线程安全的测试
 * Created on 2021/8/3 6:34 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class VectorSafeTest {
    private static Vector<Integer> vector = new Vector<>();

    public static void main(String[] args) throws InterruptedException {
        // 尽管get、remove、size都是同步，但在多线程环境中，若不在方法调用端做额外的同步措施，这个代码仍是不安全的。
        unSafe();
        safe();
    }

    private static void unSafe() throws InterruptedException {
        while (true) {
            for (int i = 0; i < 10; i++) {
                vector.add(i);
            }

            Thread removeThread = new Thread(() -> {
                for (int i = 0; i < vector.size(); i++) {
                    vector.remove(i);
                }
            });
            Thread printThread = new Thread(() -> {
                for (int i = 0; i < vector.size(); i++) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(vector.get(i));
                }
            });

            removeThread.start();
            printThread.start();

            TimeUnit.SECONDS.sleep(1);
            // 控制线程数量
            while (Thread.activeCount() > 20) ;
        }
    }

    // 客户端加入同步保证Vector访问的线程安全性
    private static void safe() throws InterruptedException {
        while (true) {
            for (int i = 0; i < 10; i++) {
                vector.add(i);
            }

            Thread removeThread = new Thread(() -> {
                synchronized (vector) {
                    for (int i = 0; i < vector.size(); i++) {
                        vector.remove(i);
                    }
                }
            });
            Thread printThread = new Thread(() -> {
                synchronized (vector) {
                    for (int i = 0; i < vector.size(); i++) {
                        try {
                            TimeUnit.MILLISECONDS.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        System.out.println(vector.get(i));
                    }
                }
            });

            removeThread.start();
            printThread.start();

            TimeUnit.SECONDS.sleep(1);
            while (Thread.activeCount() > 20) ;
        }
    }
}
