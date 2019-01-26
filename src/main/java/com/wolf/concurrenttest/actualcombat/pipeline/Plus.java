package com.wolf.concurrenttest.actualcombat.pipeline;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Description:
 * <br/> Created on 23/03/2018 10:53 AM
 *
 * @author 李超
 * @since 1.0.0
 */
public class Plus implements Runnable {

    static BlockingQueue<ComputerTransfer> blockingQueue = new LinkedBlockingQueue<ComputerTransfer>();

    @Override
    public void run() {
        for(;;) {
            try {
                ComputerTransfer take = blockingQueue.take();
                int result = take.getA() + take.getB();
                take.setB(result);
                Multiply.blockingQueue.add(take);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
