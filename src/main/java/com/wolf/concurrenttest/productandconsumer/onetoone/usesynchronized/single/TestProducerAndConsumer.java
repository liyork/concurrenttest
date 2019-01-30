package com.wolf.concurrenttest.productandconsumer.onetoone.usesynchronized.single;

/**
 * <p> Description:一个生产一个消费，使用synchronized实现,这里只处理一对一，notify而不是notifyall
 * java.lang.Object类提供了wait()、notify()、notifyAll()方法，
 *
 * 这些方法只有在synchronized或synchronized代码块中才能使用，是否就会报java.lang.IllegalMonitorStateException异常。
 *
 * 产生问题：
 * 生产者可能生产完-满了，通知消费者后又再次获取了锁然后查看满了，等待，，一直等待了。
 * 或者
 * 消费者消费完了通知生产者后又再次获取锁然后缺货了，等待。
 * 以上推论不正确：因为不能同时满足都wait的状态，生产者是满了就等待，而消费者是未满则等待，由于是两个线程，而同一时间，只有一个
 * 进入，那另一个等待进入，若这个阻塞，则另一个进入后必然条件满足，发起唤醒后，若再次获取锁那么他就等待，信号不会丢失，因为阻塞
 * 队列中只有一个线程，必然收到后唤醒。
 *
 * 由于都有synchronized，所以同一时间不论生产还是消费都只能一人进行。
 *
 * 若使用多个生产、消费，那么有可能一直保持生产-满了再唤醒生产者，消费不了，或者消费-缺货在唤醒消费者，生产不了。类似活锁
 * <p/>
 * Date: 2016/6/12
 * Time: 14:33
 *
 * @author 李超
 * @version 1.0
 * @since 1.0
 */
public class TestProducerAndConsumer {

    /**
     * @param args
     */
    public static void main(String[] args) {
        Clerk clerk = new Clerk();
        Thread producerThread = new Thread(new Producer(clerk));
        Thread consumerThread = new Thread(new Consumer(clerk));

        producerThread.start();
        consumerThread.start();
    }
}
