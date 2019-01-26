package com.wolf.concurrenttest.productandconsumer.onetoone.usesynchronized.multi;

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
public class Consumer2 implements Runnable {

    private Clerk2 clerk;

    public Consumer2(Clerk2 clerk) {
        this.clerk = clerk;
    }

    public void run() {
        System.out.println("消费者开始取走产品.\n");
        while(true) {
//            try {
//                Thread.sleep((int) (Math.random() * 10) * 100);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            clerk.getProduct();  //取产品
        }
    }

}