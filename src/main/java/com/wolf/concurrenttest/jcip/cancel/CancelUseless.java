package com.wolf.concurrenttest.jcip.cancel;

import java.math.BigInteger;
import java.util.concurrent.BlockingQueue;

/**
 * Description: 标志无效
 * 可能由于调用阻塞方法，导致线程感知不到cancel标志，这时需要用到interrupt
 * Created on 2021/7/2 1:53 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class CancelUseless {
    class BrokenPrimeProducer extends Thread {
        private final BlockingQueue<BigInteger> queue;
        private volatile boolean cancelled = false;

        public BrokenPrimeProducer(BlockingQueue<BigInteger> queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            try {
                BigInteger p = BigInteger.ONE;
                while (!cancelled) {
                    queue.put(p = p.nextProbablePrime());// 这里要是阻塞可能就不会感知到cancelled了
                }
            } catch (InterruptedException e) {

            }
        }

        public void cancel() {
            cancelled = true;
        }
    }

    void consumerPrimes() throws InterruptedException {
        BlockingQueue<BigInteger> primes = null;
        BrokenPrimeProducer producer = new BrokenPrimeProducer(primes);
        producer.start();
        try {
            while (needMorePrimes()) {
                consume(primes.take());
            }
        } finally {
            producer.cancel();
        }
    }

    private void consume(BigInteger take) {
    }

    private boolean needMorePrimes() {
        return false;
    }

}