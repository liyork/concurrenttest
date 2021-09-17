package com.wolf.concurrenttest.hcpta.deepthread;

/**
 * Description:
 * Created on 2021/9/17 6:33 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ThreadSleep {
    public static void main(String[] args) {
        new Thread(() -> {
            long startTime = System.currentTimeMillis();
            sleep(2_000L);
            long endTime = System.currentTimeMillis();
            System.out.println(String.format("Total spend %d ms", (endTime - startTime)));
        }).start();

        long startTime = System.currentTimeMillis();
        sleep(3_000L);
        long endTime = System.currentTimeMillis();
        System.out.println(String.format("main thread total spend %d ms", (endTime - startTime)));
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);  // 影响当前线程进入睡眠
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
