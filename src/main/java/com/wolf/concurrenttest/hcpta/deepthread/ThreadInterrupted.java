package com.wolf.concurrenttest.hcpta.deepthread;

import java.util.concurrent.TimeUnit;

/**
 * Description:
 * Created on 2021/9/17 9:21 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ThreadInterrupted {
    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    System.out.println(Thread.interrupted());
                }
            }
        };
        thread.setDaemon(true);
        thread.start();

        // shortly block make sure the thread is started.
        TimeUnit.MILLISECONDS.sleep(2);
        thread.interrupt();
    }
}
