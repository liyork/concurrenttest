package com.wolf.concurrenttest.stdlibtest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Description:测试使用读写锁控制操作同一个源，读写分离，读和读不锁，其他都锁
 * <p>
 * 特性
 * 重入方面:其内部的WriteLock可以获取ReadLock，但是反过来ReadLock不可以获得WriteLock
 * 升级/降级：WriteLock可以降级为ReadLock，顺序是：先获得WriteLock再获得ReadLock，然后释放WriteLock，
 * 这时候线程将保持Readlock的持有。反之不行
 * ReadLock可以被多个线程持有并且在作用时排斥任何的WriteLock，而WriteLock则是完全的互斥
 * 不管是ReadLock还是WriteLock都支持Interrupt，语义与ReentrantLock一致。
 * WriteLock支持Condition并且与ReentrantLock语义一致，而ReadLock则不能使用Condition，否则抛出UnsupportedOperationException异常。
 * <p>
 * 内部使用int32位的前16位表示读锁，后16为表示写锁
 * <p>
 * 场景：
 * 读多写少，
 * <p>
 * 以前要实现同样效果则使用synchronized+volatile变量。
 * 读时加锁判断volatile变量是否可读，不可读则等待。写时加另一个锁，写入后并修改volatile再通知所有。
 * <p>
 * 写锁持有时，可以申请获取读锁，然后释放写锁，降级为读锁。但是读锁不能获取写锁然后释放读锁升级锁，都是由于java内存可见性的目的。
 * <br/> Created on 2017/5/11 11:26
 *
 * @author 李超
 * @since 1.0.0
 */
public class ReentrantReadWriteLockTest {

    static ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();
    static ReentrantReadWriteLock.ReadLock readLock = reentrantReadWriteLock.readLock();
    static ReentrantReadWriteLock.WriteLock writeLock = reentrantReadWriteLock.writeLock();

    static CountDownLatch countDownLatch = new CountDownLatch(20);//保证尽量同时进行

    public static void main(String[] args) {

        final Cache cache = new Cache();

        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < 10; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    countDownLatch.countDown();
                    cache.get();
                }
            });
        }

        for (int i = 0; i < 10; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    countDownLatch.countDown();
                    cache.increase();
                }
            });
        }


        executorService.shutdown();
    }


    static class Cache {
        int count;//由于加锁了能保证happens-before语义，否则需要使用volatile

        public void get() {
            try {
                System.out.println("get before await");
                countDownLatch.await();
                System.out.println("get after await");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            readLock.lock();

            System.out.println(Thread.currentThread().getName() + " read value from cache,count: " + count);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                readLock.unlock();
            }

        }

        public void increase() {
            try {
                System.out.println("increase before await");
                countDownLatch.await();
                System.out.println("increase after await");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            writeLock.lock();

            System.out.println(Thread.currentThread().getName() + " set value into value,count :" + ++count);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                writeLock.unlock();
            }

        }

    }
}

