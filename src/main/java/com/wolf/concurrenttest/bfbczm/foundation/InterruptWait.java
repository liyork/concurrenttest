package com.wolf.concurrenttest.bfbczm.foundation;

import java.util.concurrent.TimeUnit;

/**
 * Description: 中断能打断阻塞的线程
 * Created on 2021/9/2 6:54 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class InterruptWait {
    public static void main(String[] args) throws InterruptedException {
        Thread threadOne = new Thread(() -> {
            try {
                System.out.println("threadOne begin sleep for 2000 seconds");
                TimeUnit.SECONDS.sleep(2000);
                System.out.println("threadOne awaking");
            } catch (InterruptedException e) {
                System.out.println("threadOne is interrupted while sleeping");
                return;
            }

            System.out.println("threadOne-leaving normally");
        });

        threadOne.start();

        TimeUnit.SECONDS.sleep(1);

        // 打断子线程的休眠，让其从sleep返回
        threadOne.interrupt();

        threadOne.join();

        System.out.println("main thread is over");
    }
}
