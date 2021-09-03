package com.wolf.concurrenttest.bfbczm.foundation;

import java.util.concurrent.TimeUnit;

/**
 * Description: 测试中断wait的线程
 * Created on 2021/9/2 8:43 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class WaitNotifyInterupt {
    static Object obj = new Object();

    public static void main(String[] args) throws InterruptedException {
        Thread threadA = new Thread(() -> {
            try {
                System.out.println("---begin---");
                synchronized (obj) {
                    obj.wait();  // 其他线程调用interrupt则这个线程抛出异常InterruptedException
                }
                System.out.println("---end---");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        threadA.start();

        TimeUnit.SECONDS.sleep(1);

        System.out.println("---begin interrupt threadA---");
        threadA.interrupt();
        System.out.println("---end interrupt threadA---");

    }
}
