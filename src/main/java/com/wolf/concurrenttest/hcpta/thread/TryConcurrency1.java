package com.wolf.concurrenttest.hcpta.thread;

import java.util.concurrent.TimeUnit;

/**
 * Description:
 * Created on 2021/9/15 1:05 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class TryConcurrency1 {
    public static void main(String[] args) throws InterruptedException {
        // 用匿名内部类方式创建线程，重写run方法
        new Thread() {
            @Override
            public void run() {
                enjoyMusic();
            }
        }.start();  // 调用start，派生一个新线程
        browseNews();
    }

    private static void browseNews() {
        for (; ; ) {
            System.out.println("Uh-hub, the good news.");
            sleep();
        }
    }

    private static void enjoyMusic() {
        for (; ; ) {
            System.out.println("Un-hub, the nic music.");
            sleep();
        }
    }

    private static void sleep() {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
