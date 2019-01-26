package com.wolf.concurrenttest.productandconsumer.disruptor;

import com.lmax.disruptor.WorkHandler;
import com.wolf.concurrenttest.productandconsumer.onetoone.useblockingqueue.Food;

/**
 * <p> Description:
 * <p/>
 * Date: 2016/6/12
 * Time: 12:59
 *
 * @author 李超
 * @version 1.0
 * @since 1.0
 */
public class Consumer implements WorkHandler<Food> {

    @Override
    public void onEvent(Food food) throws Exception {
        System.out.println(Thread.currentThread().getName()+" id:"+food.getId());
    }
}