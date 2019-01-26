package com.wolf.concurrenttest.productandconsumer.onetoone.useblockingqueue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * <p> Description:
 * 生产者与消费者模型中，要保证以下几点：
 * 1 同一时间内只能有一个生产者生产
 * 2 同一时间内只能有一个消费者消费
 * 3 生产者生产的同时消费者不能消费
 * 4 消费者消费的同时生产者不能生产
 * 5 共享空间空时消费者不能继续消费
 * 6 共享空间满时生产者不能继续生产
 *
 * LinkedBlockingQueue内部有reentrantlock保证，即同一时间只能有一个生产者或消费者操作
 *
 * 消费线程中queue.take()返回之后，对应生产线程（生产该task的线程）中queue.put()及之前的行为，对于消费线程来说都是可见的。
 * 借助queue.put()与queue.take()的偏序关系
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
        BlockingQueue<Food> foods = new LinkedBlockingQueue<Food>(10);
        Thread produce = new Thread(new Producer(foods));
        Thread consume = new Thread(new Consumer(foods));
        produce.start();
        consume.start();
    }
}
