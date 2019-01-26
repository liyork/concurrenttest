package com.wolf.concurrenttest.lock.condition;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 使用锁的条件控制，
 * 如果数组满了则不能放入，放入同时通知*等待取的线程*一起竞争
 * 如果数组空了则不能取，取出的同时通知*等待放的线程*一起竞争
 *
 * 这个仅用来测试条件控制，但是实际应用时，这样可能会阻塞并发读的线程，应该使用读写锁去控制。相比锁+一个队列来说进步了
 *
 * <br/> Created on 2017/5/11 17:39
 *
 * @author 李超
 * @since 1.0.0
 */
public class MyBoundedBufferTest {

    private final Lock lock = new ReentrantLock();
    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();

    private final Object[] items = new Object[100];
    private int putptr, takeptr, count;

    public void put(Object x) throws InterruptedException {
        System.out.println("put wait lock");
        lock.lock();
        System.out.println("put get lock");
        try {
            while(count == items.length) {
                System.out.println("buffer full, please wait");
                notFull.await();
            }

            items[putptr] = x;
            if(++putptr == items.length) putptr = 0;
            ++count;

            Thread.sleep(3000);
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }


    public Object take() throws InterruptedException {
        System.out.println("take wait lock");
        lock.lock();
        System.out.println("take get lock");
        try {
            while(count == 0) {
                System.out.println("no elements, please wait");
                notEmpty.await();
            }
            Object x = items[takeptr];
            if(++takeptr == items.length) takeptr = 0;
            --count;

            Thread.sleep(3000);
            notFull.signal();
            return x;
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        final MyBoundedBufferTest boundedBuffer = new MyBoundedBufferTest();

        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("t1 run");
                for(int i = 0; i < 1000; i++) {
                    try {
                        System.out.println("putting..");
                        boundedBuffer.put(i);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        });

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i = 0; i < 1000; i++) {
                    try {
                        Object val = boundedBuffer.take();
                        System.out.println(val);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        });

        t1.start();
        t2.start();
    }
}
