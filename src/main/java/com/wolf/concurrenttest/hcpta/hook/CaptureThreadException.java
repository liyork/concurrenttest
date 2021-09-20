package com.wolf.concurrenttest.hcpta.hook;

import java.util.concurrent.TimeUnit;

/**
 * Description:
 * Created on 2021/9/19 11:32 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class CaptureThreadException {
    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            System.out.println(t.getName() + " occur exception");
            e.printStackTrace();
        });

        final Thread thread = new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // here will throw unchecked exception
            System.out.println(1 / 0);
        }, "test-Thread");
        thread.start();
    }
}
