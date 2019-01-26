package com.wolf.concurrenttest.productandconsumer.inturn;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Description:
 * <br/> Created on 14/03/2018 10:23 AM
 *
 * @author 李超
 * @since 1.0.0
 */
public class FacadeSelector {

    public void start(){
        new SplitWorker().start();
        new ConsumerManager().start();
        System.out.println("FacadeSelector is start.");
    }

    static LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<>(10);

    public void connect(int id) {
        try {
            queue.put(new Runnable() {
                @Override
                public void run() {
                    System.out.println(Thread.currentThread().getName()+" connect id:"+id);
                    try {
                        Thread.sleep(400);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
