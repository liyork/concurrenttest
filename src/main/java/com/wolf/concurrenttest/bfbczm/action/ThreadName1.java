package com.wolf.concurrenttest.bfbczm.action;

import java.util.concurrent.TimeUnit;

/**
 * Description: 指定名称的线程
 * Created on 2021/9/14 6:27 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ThreadName1 {
    static final String THREAD_SAVE_ORDER = "THREAD_SAVE_ORDER";
    static final String THREAD_SAVE_ADDR = "THREAD_SAVE_ADDR";

    public static void main(String[] args) {
        Thread oneThread = new Thread(() -> {
            System.out.println("保存订单的线程");
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            throw new NullPointerException();
        }, THREAD_SAVE_ORDER);

        Thread twoThread = new Thread(() -> {
            System.out.println("保存收货地址的线程");
        }, THREAD_SAVE_ADDR);

        oneThread.start();
        twoThread.start();
    }
}
