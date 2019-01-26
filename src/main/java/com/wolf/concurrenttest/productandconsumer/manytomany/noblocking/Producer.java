package com.wolf.concurrenttest.productandconsumer.manytomany.noblocking;

import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

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
public class Producer implements Runnable {

    static AtomicInteger atomicInteger = new AtomicInteger(1);

    private ConcurrentLinkedQueue<Food> foods;

    public Producer(ConcurrentLinkedQueue<Food> foods) {
        this.foods = foods;
    }

    @Override
    public void run() {
        Random random = new Random();

        //todo 由于ConcurrentLinkedQueue无限制，所以可能要考虑到达一定程度如何办？不能内存溢出
        while(true) {
            try {
                Thread.sleep(random.nextInt(100));
                Food food = new Food("食品" + atomicInteger.getAndIncrement());
                System.out.println(Thread.currentThread().getName()+",生产:"+food.getName());
                foods.add(food);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
