package com.wolf.concurrenttest.bfbczm.action;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Description: 若task抛出异常则后续都不执行
 * Created on 2021/9/13 9:49 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class TestTimer {
    static Timer timer = new Timer();

    public static void main(String[] args) {
        // 任务1，延迟500ms执行
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("---one task---");
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                throw new RuntimeException("error ");
            }
        }, 500);

        // 任务2，延迟1000ms执行
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                for (; ; ) {
                    System.out.println("---two task---");
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, 1000);
    }
}
