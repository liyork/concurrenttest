package com.wolf.concurrenttest.productandconsumer.onetoone.useblockingqueue;

import java.util.concurrent.BlockingQueue;

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

    private BlockingQueue<Food> foods;

    Consumer(BlockingQueue<Food> foods) {
        this.foods = foods;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(1000);  //用于测试当生产者生产满10个食品后是否进入等待状态
            while(true) {
                Thread.sleep(1000);
                //当容器里面的食品数量为空时，那么在while里面该食品容器(阻塞队列)会自动阻塞  wait状态 等待生产
                Food food = foods.take();
                System.out.println("消费" + food.getName());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}