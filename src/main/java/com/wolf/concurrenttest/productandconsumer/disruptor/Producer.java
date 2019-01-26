package com.wolf.concurrenttest.productandconsumer.disruptor;

import com.lmax.disruptor.RingBuffer;
import com.wolf.concurrenttest.productandconsumer.onetoone.useblockingqueue.Food;

import java.nio.ByteBuffer;

/**
 * Description:
 * <br/> Created on 22/03/2018 8:52 PM
 *
 * @author 李超
 * @since 1.0.0
 */
public class Producer {

    private final RingBuffer<Food> ringBuffer;

    public Producer(RingBuffer<Food> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    public void pushData(ByteBuffer byteBuffer) {
        long sequence = ringBuffer.next();
        Food food = ringBuffer.get(sequence);
        food.setId(byteBuffer.getInt(0));

        ringBuffer.publish(sequence);

    }
}
