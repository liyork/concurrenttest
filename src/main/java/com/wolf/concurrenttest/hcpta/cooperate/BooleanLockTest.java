package com.wolf.concurrenttest.hcpta.cooperate;

import akka.dispatch.forkjoin.ThreadLocalRandom;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * Description:
 * Created on 2021/9/19 8:14 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class BooleanLockTest {
    private final Lock lock = new BooleanLock();

    public void syncMethod() throws InterruptedException {
        lock.lock();
        try {
            int randomInt = ThreadLocalRandom.current().nextInt(10);
            System.out.println(Thread.currentThread() + " get the lock");
            TimeUnit.SECONDS.sleep(randomInt);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {// 用try-finally保证锁释放
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        BooleanLockTest blt = new BooleanLockTest();
        IntStream.range(0, 10)
                .mapToObj(i -> new Thread(() -> {
                    try {
                        blt.syncMethod();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }))
                .forEach(Thread::start);
    }
}
