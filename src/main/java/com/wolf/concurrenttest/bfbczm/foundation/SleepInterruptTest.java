package com.wolf.concurrenttest.bfbczm.foundation;

import java.util.concurrent.TimeUnit;

/**
 * Description: 中断sleep的线程
 * Created on 2021/9/2 1:33 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class SleepInterruptTest {
    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(() -> {

            try {
                System.out.println("child threadA is in sleep");
                TimeUnit.SECONDS.sleep(5);
                System.out.println("child threadA is in awaked");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        thread.start();

        TimeUnit.SECONDS.sleep(2);
        // 主线程中断子线程
        thread.interrupt();
    }
}
