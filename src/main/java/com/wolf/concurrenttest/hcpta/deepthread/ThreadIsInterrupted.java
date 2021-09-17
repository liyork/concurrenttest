package com.wolf.concurrenttest.hcpta.deepthread;

import java.util.concurrent.TimeUnit;

/**
 * Description:
 * Created on 2021/9/17 9:13 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ThreadIsInterrupted {
    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    // do nothing, just empty loop.
                }
            }
        };

        thread.start();
        TimeUnit.MILLISECONDS.sleep(2);

        System.out.printf("thread is interrupted ? %s\n", thread.isInterrupted());
        thread.interrupt();
        System.out.printf("thread is interrupted ? %s\n", thread.isInterrupted());
    }
}
