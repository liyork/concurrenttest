package com.wolf.concurrenttest.hcpta.deepthread;

import java.util.concurrent.TimeUnit;

/**
 * Description:
 * Created on 2021/9/17 9:07 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ThreadInterrupt {
    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(() -> {
            try {
                TimeUnit.MINUTES.sleep(1);
            } catch (InterruptedException e) {
                System.out.println("oh, i am be interrupted");
            }
        });
        thread.start();

        // short block and make sure thread is started
        TimeUnit.MILLISECONDS.sleep(2000);
        thread.interrupt();
        System.out.println("main finish");
    }
}
