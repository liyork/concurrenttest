package com.wolf.concurrenttest.jcip.cancel;

import java.math.BigInteger;
import java.util.concurrent.BlockingQueue;

/**
 * Description: 使用Interrupt进行取消
 * Created on 2021/7/2 10:32 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class CancellUsingInterrupt {
    class PrimeProducer extends Thread {
        private final BlockingQueue<BigInteger> queue;

        public PrimeProducer(BlockingQueue<BigInteger> queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            try {
                BigInteger p = BigInteger.ONE;
                while (!Thread.currentThread().isInterrupted()) {// 检查
                    queue.put(p = p.nextProbablePrime());// 检查
                }
            } catch (InterruptedException e) {
                // allow thread to exit
            }
        }

        public void cancel() {
            interrupt();
        }
    }
}
