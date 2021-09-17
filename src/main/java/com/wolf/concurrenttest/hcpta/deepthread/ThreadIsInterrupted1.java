package com.wolf.concurrenttest.hcpta.deepthread;

import java.util.concurrent.TimeUnit;

/**
 * Description:
 * Created on 2021/9/17 9:13 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ThreadIsInterrupted1 {
    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        TimeUnit.MINUTES.sleep(1);
                    } catch (InterruptedException e) {  // 捕获中断信号并清除flag
                        // ignore the exception
                        // here the interrupt flag will be clear
                        System.out.printf("i am be interrupted ? %s\n", isInterrupted());
                    }
                }
            }
        };

        thread.setDaemon(true);
        thread.start();
        TimeUnit.MILLISECONDS.sleep(2);

        System.out.printf("thread is interrupted ? %s\n", thread.isInterrupted());
        thread.interrupt();
        TimeUnit.MILLISECONDS.sleep(2);
        System.out.printf("thread is interrupted ? %s\n", thread.isInterrupted());
    }
}
