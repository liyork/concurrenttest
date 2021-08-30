package com.wolf.concurrenttest.taojcp.lock;

import com.wolf.concurrenttest.taojcp.thread.SleepUtils;
import org.junit.Test;

/**
 * Description: 测试TwinsLock
 * 可看到线程名称，成对输出，即同一时刻只有两个线程能获取到锁
 * Created on 2021/8/29 7:09 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class TwinsLockTest {
    TwinsLock lock = new TwinsLock();

    @Test
    public void test() {
        for (int i = 0; i < 10; i++) {
            Thread w = new Thread(new Worker());
            w.setDaemon(true);
            w.start();
        }
        // 间隔1s换行
        for (int i = 0; i < 10; i++) {
            SleepUtils.second(1);
            System.out.println();
        }
    }

    class Worker implements Runnable {
        @Override
        public void run() {
            while (true) {
                lock.lock();
                try {
                    System.out.println(Thread.currentThread().getName());
                    SleepUtils.second(1);
                } finally {
                    lock.unlock();
                }
            }
        }
    }
}
