package com.wolf.concurrenttest.productandconsumer.onetoone.usesynchronized.multi;

/**
 * <p> Description:
 * 假死的原因是由于使用了notify——产生了信号丢失，那么如果总是唤醒的同类，就有可能大家都在等待。
 * <p/>
 * Date: 2016/6/12
 * Time: 14:33
 *
 * @author 李超
 * @version 1.0
 * @since 1.0
 */
public class TestProducerAndConsumer2 {

    /**
     * @param args
     */
    public static void main(String[] args) throws InterruptedException {
        Clerk2 clerk = new Clerk2();
        Producer2 producer2 = new Producer2(clerk);
        Thread producerThread1 = new Thread(producer2);
        Thread producerThread2 = new Thread(producer2);
        Consumer2 consumer2 = new Consumer2(clerk);
        Thread consumerThread1 = new Thread(consumer2);
        Thread consumerThread2 = new Thread(consumer2);

        producerThread1.start();
        producerThread2.start();
        consumerThread1.start();
        consumerThread2.start();

        Thread.sleep(5000);
        Thread[] threads = new Thread[Thread.currentThread().getThreadGroup().activeCount()];
        Thread.currentThread().getThreadGroup().enumerate(threads);
        for (Thread thread : threads) {
            System.out.println(thread.getName()+" "+thread.getState());
        }

    }
}
