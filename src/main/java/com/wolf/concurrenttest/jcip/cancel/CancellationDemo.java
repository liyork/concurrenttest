package com.wolf.concurrenttest.jcip.cancel;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Description: using a volatile Field to hold cancellation state
 * 线程自己检查
 * Created on 2021/7/2 1:27 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class CancellationDemo {
    @ThreadSafe
    class primeGenerator implements Runnable {
        @GuardedBy("this")
        private final List<BigInteger> primes = new ArrayList<>();
        private volatile boolean cancelled;

        @Override
        public void run() {
            BigInteger p = BigInteger.ONE;
            while (!cancelled) {
                p = p.nextProbablePrime();
                synchronized (this) {
                    primes.add(p);
                }
            }
        }

        public void cancel() {
            cancelled = true;
        }

        public synchronized List<BigInteger> get() {// 需要上锁，因为另一个线程不断放入，这里一旦get就涉及迭代(要拷贝数据)，就暂时不允许变更了
            return new ArrayList<>(primes);
        }
    }

    // 生成1s的数，就被取消
    List<BigInteger> aSecondOfPrimes() throws InterruptedException {
        primeGenerator generator = new primeGenerator();
        new Thread(generator).start();
        try {
            TimeUnit.SECONDS.sleep(1);
        } finally {
            generator.cancel();
        }
        return generator.get();
    }
}
