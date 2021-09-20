package com.wolf.concurrenttest.hcpta.cooperate;

import akka.dispatch.forkjoin.ThreadLocalRandom;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Description: 可被中断测试
 * Created on 2021/9/19 8:14 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class BooleanLockTest2 {
    private final Lock lock = new BooleanLock();

    public void syncMethodTimeoutable() {
        try {
            lock.lock(1000);
            int randomInt = ThreadLocalRandom.current().nextInt(10);
            System.out.println(Thread.currentThread() + " get the lock");
            TimeUnit.SECONDS.sleep(randomInt);
        } catch (InterruptedException | TimeoutException e) {
            e.printStackTrace();
        } finally {  // 这里捕获两个异常没问题，理论上，不应该lock放到try中，像这样，可能unlock其他人的锁了，不过里面有判断不是当前线程就无效，也没事
            lock.unlock();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        BooleanLockTest2 blt = new BooleanLockTest2();
        new Thread(blt::syncMethodTimeoutable, "t1").start();

        TimeUnit.MILLISECONDS.sleep(2);

        Thread t2 = new Thread(blt::syncMethodTimeoutable, "t2");
        t2.start();

        TimeUnit.MILLISECONDS.sleep(10);
    }
}
