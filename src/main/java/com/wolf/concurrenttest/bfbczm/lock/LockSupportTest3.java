package com.wolf.concurrenttest.bfbczm.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * Description: 测试，仅中断才退出
 * Created on 2021/9/5 11:43 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class LockSupportTest3 {
    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(() -> {
            System.out.println("child thread begin park!");

            // 只有被中断才会退出循环
            while (!Thread.currentThread().isInterrupted()) {
                // 调用park，挂起自己
                LockSupport.park();
            }

            System.out.println("child thread unpark!");
        });

        thread.start();

        TimeUnit.SECONDS.sleep(1);

        System.out.println("main thread begin unpark!");
        // 中断子线程
        thread.interrupt();
    }
}
