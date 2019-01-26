package com.wolf.concurrenttest.productandconsumer.inturn;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Description:
 * <br/> Created on 14/03/2018 10:25 AM
 *
 * @author 李超
 * @since 1.0.0
 */
public class SplitWorker implements Runnable {

    static List<LinkedBlockingQueue<Runnable>> subQueues = new ArrayList<>();

    public void start(){
        subQueues.add(new LinkedBlockingQueue<>(3));
        subQueues.add(new LinkedBlockingQueue<>(3));
        subQueues.add(new LinkedBlockingQueue<>(3));
        new Thread(this).start();
        System.out.println("SplitWorker is start.");
    }

    @Override
    public void run() {
        for (; ; ) {

            LinkedBlockingQueue<Runnable> queue = FacadeSelector.queue;
            Runnable take = null;
            try {
                take = queue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            LinkedBlockingQueue<Runnable> subQueue = getSubQueue();
            try {
                subQueue.put(take);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private LinkedBlockingQueue<Runnable> getSubQueue() {
        long index = System.nanoTime() % subQueues.size();
        return subQueues.get((int) index);
    }
}
