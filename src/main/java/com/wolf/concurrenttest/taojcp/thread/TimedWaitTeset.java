package com.wolf.concurrenttest.taojcp.thread;

import java.util.concurrent.TimeUnit;

/**
 * Description: 超时等待获取值
 * Created on 2021/8/27 12:29 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class TimedWaitTeset {
    private Long result;

    public synchronized Object get(long mills) throws InterruptedException {  // 先加锁
        long future = System.currentTimeMillis() + mills;
        long remaining = mills;
        // 当超时大于0并且result没有值则继续等待
        while ((result == null) && remaining > 0) {
            this.wait(remaining);
            remaining = future - System.currentTimeMillis();
        }
        return result;
    }

    void req() {
        new Thread(() -> {
            SleepUtils.second(5);
            synchronized (this) {  // 保证可见性
                result = 1L;
                this.notify();
            }
        }, "send Thread").start();
    }

    public static void main(String[] args) throws InterruptedException {
        TimedWaitTeset timedWaitTeset = new TimedWaitTeset();
        timedWaitTeset.req();

        long mills = TimeUnit.SECONDS.toMillis(3);  // 切换不同等待时间，观察效果：TimeUnit.SECONDS.toMillis(6);
        Object o = timedWaitTeset.get(mills);
        System.out.println("result is: " + o);
    }
}
