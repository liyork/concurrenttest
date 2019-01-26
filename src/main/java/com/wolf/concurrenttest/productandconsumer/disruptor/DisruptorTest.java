package com.wolf.concurrenttest.productandconsumer.disruptor;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.wolf.concurrenttest.productandconsumer.onetoone.useblockingqueue.Food;
import com.wolf.concurrenttest.threadpool.MyThreadFactory;

import java.nio.ByteBuffer;

/**
 * <p> Description:
 * cpu的缓存行一般为32字节到128字节
 *
 * RingBuffer内部填充了缓存行
 *
 * 内部构造使用this.entries = new Object[sequencer.getBufferSize() + 2 * BUFFER_PAD];
 * 为了让每个元素单独占缓存行，不会多个变量占用缓存航引起相互操作失效
 *
 *
 * <p/>
 * Date: 2016/6/12
 * Time: 12:58
 *
 * @author 李超
 * @version 1.0
 * @since 1.0
 */
public class DisruptorTest {

    public static void main(String[] args) throws InterruptedException {
        FoodFactory foodFactory = new FoodFactory();

        MyThreadFactory myThreadFactory = new MyThreadFactory("testpool");
        int ringBufferSize = 1024;//需要时2的n次幂，提前分配1024个food对象

        //BlockingWaitStrategy节省cpu，等待、唤醒，高并发时性能不好
        //SleepingWaitStrategy适合有一定延迟的。异步日志
        //YieldingWaitStrategy适合低延迟。逻辑cpu数高于消费者
        //BusySpinWaitStrategy死循环，更低延迟，但是cpu使用很高。物理cpu数要高于消费者
        Disruptor<Food> foodDisruptor = new Disruptor<>(foodFactory, ringBufferSize,
                myThreadFactory, ProducerType.MULTI, new BlockingWaitStrategy());

        foodDisruptor.handleEventsWithWorkerPool(
                new Consumer(),
                new Consumer(),
                new Consumer(),
                new Consumer());

        foodDisruptor.start();

        RingBuffer<Food> ringBuffer = foodDisruptor.getRingBuffer();
        Producer producer = new Producer(ringBuffer);
        ByteBuffer bb = ByteBuffer.allocate(8);
        for (int i = 0; true ; i++) {
            bb.putInt(0, i);//放入第0位置，在生产者中获取然后放入food中再发布
            producer.pushData(bb);
            Thread.sleep(100);
            System.out.println("add data "+i);
        }
    }
}
