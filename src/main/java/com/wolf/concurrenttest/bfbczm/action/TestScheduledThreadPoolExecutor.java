package com.wolf.concurrenttest.bfbczm.action;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Description: 测试ScheduledThreadPoolExecutor
 * Created on 2021/9/13 9:59 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class TestScheduledThreadPoolExecutor {
    static ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);

    public static void main(String[] args) {
        scheduledThreadPoolExecutor.schedule(() -> {
            System.out.println("---one task---");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            throw new RuntimeException("error ");
        }, 500, TimeUnit.MICROSECONDS);

        scheduledThreadPoolExecutor.schedule(() -> {
            for (; ; ) {
                System.out.println("---two task---");
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, 1000, TimeUnit.MICROSECONDS);

        scheduledThreadPoolExecutor.shutdown();
    }
}
