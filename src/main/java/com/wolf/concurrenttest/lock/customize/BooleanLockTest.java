package com.wolf.concurrenttest.lock.customize;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.IntStream;

/**
 * Description:
 * <br/> Created on 2019-01-27
 *
 * @author 李超
 * @since 1.0.0
 */
public class BooleanLockTest {

    private BooleanLock lock = new BooleanLock();

    public static void main(String[] args) throws InterruptedException {

        //testBase();
//        testInterrupt();
        testTimeout();
    }

    private static void testBase() {

        BooleanLockTest booleanLockTest = new BooleanLockTest();

        IntStream.range(0, 10).mapToObj(i -> new Thread(booleanLockTest::syncMethod))
                .forEach(Thread::start);
    }

    private static void testInterrupt() throws InterruptedException {

        BooleanLockTest booleanLockTest = new BooleanLockTest();
        new Thread(booleanLockTest::syncMethod, "T1").start();
        TimeUnit.MILLISECONDS.sleep(200);
        Thread t2 = new Thread(booleanLockTest::syncMethod, "T2");
        t2.start();
        TimeUnit.MILLISECONDS.sleep(10);
        t2.interrupt();
    }

    private static void testTimeout() throws InterruptedException {

        BooleanLockTest booleanLockTest = new BooleanLockTest();
        new Thread(booleanLockTest::syncMethod, "t1").start();
        TimeUnit.MILLISECONDS.sleep(2);
        new Thread(booleanLockTest::syncMethodTimeoutable, "t2").start();
        TimeUnit.MILLISECONDS.sleep(10);
    }

    private void syncMethod() {

        try {
            lock.lock();

            int randomInt = ThreadLocalRandom.current().nextInt(10);
            System.out.println(Thread.currentThread() + " get the lock.");
            TimeUnit.SECONDS.sleep(randomInt);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    private void syncMethodTimeoutable() {

        try {
            lock.lock(1000);

            System.out.println(Thread.currentThread() + " get the lock.");
            int randomInt = ThreadLocalRandom.current().nextInt(10);
            TimeUnit.SECONDS.sleep(randomInt);
        } catch (InterruptedException | TimeoutException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
