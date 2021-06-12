package com.wolf.concurrenttest.jjzl.shutdown;

import java.util.concurrent.TimeUnit;

/**
 * Description: 演示Daemon线程
 * 当前jvm内没有非守护进程时退出
 * Created on 2021/5/30 8:30 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class DaemonDemo {
    public static void main(String[] args) throws InterruptedException {
        long startTime = System.nanoTime();
        Thread thread = new Thread(() -> {
            try {
                TimeUnit.DAYS.sleep(Long.MAX_VALUE);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "Daemon-T");
        //thread.setDaemon(true);// 默认false，则程序不会退出
        thread.start();
        TimeUnit.SECONDS.sleep(5);
        System.out.println("main quit " + nano2Second(startTime) + " s");
    }

    private static long nano2Second(long startTime) {
        return (System.nanoTime() - startTime) / 1000 / 1000 / 1000;
    }
}
