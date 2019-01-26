package com.wolf.concurrenttest.productandconsumer.disruptor;

import com.lmax.disruptor.EventFactory;
import com.wolf.concurrenttest.productandconsumer.onetoone.useblockingqueue.Food;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Description:
 * <br/> Created on 22/03/2018 8:50 PM
 *
 * @author 李超
 * @since 1.0.0
 */
public class FoodFactory implements EventFactory<Food>{

    static AtomicInteger count = new AtomicInteger(1);
    @Override
    public Food newInstance() {
        System.out.println("FoodFactory.newInstance count:"+count.getAndIncrement());
        return new Food();
    }
}
