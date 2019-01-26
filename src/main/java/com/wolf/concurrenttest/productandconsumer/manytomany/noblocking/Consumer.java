package com.wolf.concurrenttest.productandconsumer.manytomany.noblocking;

import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

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
public class Consumer implements Runnable {

    private ConcurrentLinkedQueue<Food> foods;

    Consumer(ConcurrentLinkedQueue<Food> foods) {
        this.foods = foods;
    }

    @Override
    public void run() {
        Random random = new Random();
        try {
            Thread.sleep(1000);
            while(true) {
                Thread.sleep(random.nextInt(100));
                Food food = foods.poll();//todo 需要判断为空,是否进行其他操作
                System.out.println(Thread.currentThread().getName()+",消费:" + food.getName());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}