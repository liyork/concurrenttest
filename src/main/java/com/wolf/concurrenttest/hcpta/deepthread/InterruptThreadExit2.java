package com.wolf.concurrenttest.hcpta.deepthread;

import com.wolf.concurrenttest.taojcp.thread.SleepUtils;

import java.util.concurrent.TimeUnit;

/**
 * Description:
 * Created on 2021/9/17 1:20 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class InterruptThreadExit2 {
    public static void main(String[] args) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                System.out.println("i will start work");
                for (; ; ) {
                    // working.
                    try {
                        TimeUnit.MILLISECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
                System.out.println("i will be exiting");
            }
        };

        thread.start();

        SleepUtils.second(1);
        System.out.println("system will be shutdown.");
        thread.interrupt();
    }
}
