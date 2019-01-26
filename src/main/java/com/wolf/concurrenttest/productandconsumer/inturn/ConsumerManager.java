package com.wolf.concurrenttest.productandconsumer.inturn;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Description:
 * <br/> Created on 14/03/2018 10:30 AM
 *
 * @author 李超
 * @since 1.0.0
 */
public class ConsumerManager implements Runnable {

    public void start() {
        new Thread(this).start();
        System.out.println("ConsumerManager is start.");
    }

    @Override
    public void run() {
        List<LinkedBlockingQueue<Runnable>> subQueues = SplitWorker.subQueues;
        for (int i = 0; i < subQueues.size(); i++) {
            LinkedBlockingQueue<Runnable> runnables = subQueues.get(i);
            new Thread(new Consumer(runnables)).start();
        }
    }

    private static class Consumer implements Runnable {

        LinkedBlockingQueue<Runnable> queue;

        public Consumer(LinkedBlockingQueue<Runnable> queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            for (; ; ) {
                try {
                    Runnable take = queue.take();
                    take.run();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
