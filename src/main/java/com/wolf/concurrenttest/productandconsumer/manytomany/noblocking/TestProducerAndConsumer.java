package com.wolf.concurrenttest.productandconsumer.manytomany.noblocking;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * <p> Description:
 * 无阻塞队列实现生产者消费者之间沟通。
 * <p/>
 * Date: 2016/6/12
 * Time: 12:58
 *
 * @author 李超
 * @version 1.0
 * @since 1.0
 */
public class TestProducerAndConsumer {

    public static void main(String[] args) {
        //固定容器大小为10
        ConcurrentLinkedQueue<Food> foods = new ConcurrentLinkedQueue<Food>();
        Producer producer = new Producer(foods);
        Consumer consumer = new Consumer(foods);
        for (int i = 0; i < 10; i++) {
            Thread produce = new Thread(producer);
            Thread consume = new Thread(consumer);
            produce.start();
            consume.start();
        }
    }
}
