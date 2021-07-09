package com.wolf.concurrenttest.jcip.testing;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 测试SemaphoreBoundedBuffer读写
 * Producer-consumer test program for SemaphoreBoundedBuffer
 */
public class PutTakeTest {
    protected static final ExecutorService pool = Executors.newCachedThreadPool();
    protected CyclicBarrier barrier;
    protected final SemaphoreBoundedBuffer<Integer> bb;
    protected final int nTrials;// 循环放、取次数
    protected final int nPairs;// 开启多少个p-c线程对
    protected final AtomicInteger putSum = new AtomicInteger(0);
    protected final AtomicInteger takeSum = new AtomicInteger(0);

    public PutTakeTest(int capacity, int npairs, int ntrials) {
        this.bb = new SemaphoreBoundedBuffer<>(capacity);
        this.nTrials = ntrials;
        this.nPairs = npairs;
        this.barrier = new CyclicBarrier(npairs * 2 + 1); // npairs个对儿+main线程
    }

    void test() {
        try {
            for (int i = 0; i < nPairs; i++) {
                pool.execute(new Producer());
                pool.execute(new Consumer());
            }
            barrier.await(); // wait for all threads to be ready  开始执行，尽量让所有线程同时执行
            barrier.await(); // wait for all threads to finish    收集数据
            assert (putSum.get() == takeSum.get()); // 主线程比较两边结果
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // medium-quality random number generator suitable for testing
    static int xorShift(int y) {
        y ^= (y << 6);
        y ^= (y >>> 21);
        y ^= (y << 7);
        return y;
    }

    class Producer implements Runnable {
        public void run() {
            try {
                int seed = (this.hashCode() ^ (int) System.nanoTime());
                int sum = 0;
                barrier.await(); // 等待一起开始
                // 放入队列并计算总和
                for (int i = nTrials; i > 0; --i) {
                    bb.put(seed);
                    // using a per thread checksum that is combined at the end of the test run
                    // so as to add no more synchronization or contention than rquired to test the buffer
                    sum += seed;
                    seed = xorShift(seed);
                }
                // 放入结果，用cas保证最终放入
                putSum.getAndAdd(sum);
                barrier.await(); // 等待一起结束
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    class Consumer implements Runnable {
        public void run() {
            try {
                barrier.await();
                int sum = 0;
                // 不断从队列取，计算总和
                for (int i = nTrials; i > 0; --i) {
                    sum += bb.take();
                }
                // 放入总和用cas
                takeSum.getAndAdd(sum);
                barrier.await();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        new PutTakeTest(10, 100, 100000).test(); // sample parameters
        pool.shutdown();
    }
}
