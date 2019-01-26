package com.wolf.concurrenttest.productandconsumer.onetoone.usesynchronized;

/**
 * <p> Description:
 * <p/>
 * Date: 2016/6/12
 * Time: 14:33
 *
 * @author 李超
 * @version 1.0
 * @since 1.0
 */
public class Clerk {

    //最大产品数
    private static final int MAX_PRODUCT = 1;

    //最小产品数
    private static final int MIN_PRODUCT = 0;

    //默认为0个产品
    private int product = 0;


    /**
     * 生产者生产出来的产品交给店员
     */
    public synchronized void addProduct() {
        if(this.product >= MAX_PRODUCT) {
            try {
                System.out.println("产品已满,进入等候");
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return;
        }

        this.product++;
        System.out.println("生产者生产第" + this.product + "个产品.");
        notifyAll();   //通知等待区的消费者可以取出产品了
    }

    /**
     * 消费者从店员取产品
     */
    public synchronized void getProduct() {
        if(this.product <= MIN_PRODUCT) {
            try {
                System.out.println("缺货,进入等待");
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return;
        }

        System.out.println("消费者取走了第" + this.product + "个产品.");
        this.product--;
        notifyAll();   //通知等待去的生产者可以生产产品了
    }
}
