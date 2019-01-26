package com.wolf.concurrenttest.productandconsumer.onetoone.usesynchronized;

/**
 * <p> Description:一个生产一个消费，使用synchronized实现,这里只处理一对一，多对多也没用，因为方法同步，只有一个线程进入，锁定的还是一个
 * 对象，一唤醒都醒了。
 * java.lang.Object类提供了wait()、notify()、notifyAll()方法，
 * 这些方法只有在synchronized或synchronized代码块中才能使用，是否就会报java.lang.IllegalMonitorStateException异常。
 *
 * 产生问题：生产者可能生产完通知消费者后又再次获取了锁然后再放入满了，等待。
 * 消费者可能消费完了通知生产者后又再次获取锁然后缺货了，等待。
 * 这倒也好，本身也是查看-满足/不满足-放入/等待。
 * 由于都有synchronized所以，同一时间不论生产还是消费都只能一人进行。
 *
 * 若使用多个生产、消费，那么有可能一直保持生产-满了再唤醒生产者，消费不了，或者消费-缺货在唤醒消费者，生产不了。
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
