package com.wolf.concurrenttest.hcpta.thread;

import java.util.concurrent.TimeUnit;

/**
 * Description: 只要当前线程状态不是0，重复start就报错
 * Created on 2021/9/15 10:07 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ReStartThread {
    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        System.out.println(thread.getState());
        thread.start();
        System.out.println(thread.getState());
        //thread.start();  // 紧接着再次启动

        TimeUnit.SECONDS.sleep(3);

        System.out.println(thread.getState());
        System.out.println(thread.isAlive());
        thread.start();  // 企图重新激活线程
    }
}
