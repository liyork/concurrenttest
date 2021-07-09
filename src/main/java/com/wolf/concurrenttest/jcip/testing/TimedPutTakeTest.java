package com.wolf.concurrenttest.jcip.testing;

import java.util.concurrent.CyclicBarrier;

/**
 * Testing with a barrier-based timer，分别测试不同容量不同线程的吞吐量
 * to measure the time taken for a run
 * we can lear several things from running it.
 * + throughput of the producer-consumer handoff operation for various combinations of parameters
 * + how the bounded buffer scales with different numbers of threads.
 * + how we might select the bound size
 * answering these questions requres running the test for various combinations of parameters.
 * the primary purpose of this test is to measure what constraints the producer-consumer handoff via the bounded buffer imposes on overall throughput
 * <p>
 * Pairs: 1	Throughput: 11774 ns/item	Throughput: 8908 ns/item
 * Pairs: 2	Throughput: 12043 ns/item	Throughput: 12670 ns/item
 * Pairs: 4	Throughput: 12778 ns/item	Throughput: 11953 ns/item
 * Pairs: 8	Throughput: 12878 ns/item	Throughput: 13847 ns/item
 * Pairs: 16	Throughput: 12960 ns/item	Throughput: 12193 ns/item
 * Pairs: 32	Throughput: 12105 ns/item	Throughput: 12094 ns/item
 */
public class TimedPutTakeTest extends PutTakeTest {
    //所有线程都开始时开始计数
    private BarrierTimer timer = new BarrierTimer();

    public TimedPutTakeTest(int cap, int pairs, int trials) {
        super(cap, pairs, trials);
        barrier = new CyclicBarrier(nPairs * 2 + 1, timer);
    }

    // Barrier-based timer
    class BarrierTimer implements Runnable {
        boolean started;
        long startTime;
        long endTime;

        @Override
        public synchronized void run() {
            long t = System.nanoTime();
            if (!started) {
                started = true;
                startTime = t;
            } else {
                endTime = t;
            }
        }

        public synchronized void clear() {
            started = false;
        }

        public synchronized long getTime() {
            return endTime - startTime;
        }
    }

    // testing with a barrier-base timer
    public void test() {
        try {
            timer.clear();
            for (int i = 0; i < nPairs; i++) {
                pool.execute(new PutTakeTest.Producer());
                pool.execute(new PutTakeTest.Consumer());
            }
            barrier.await();
            barrier.await();
            long nsPerItem = timer.getTime() / (nPairs * (long) nTrials);//每对放入100000数据所用的时间
            System.out.print("Throughput: " + nsPerItem + " ns/item");
            assert (putSum.get() == takeSum.get());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws Exception {
        int tpt = 100000; // trials per thread
        for (int cap = 1; cap <= 1000; cap *= 10) {// buf cap of 1, 10, 100, 1000
            System.out.println("Capacity: " + cap);
            for (int pairs = 1; pairs <= 128; pairs *= 2) {
                // 每个案例测试两遍
                TimedPutTakeTest t = new TimedPutTakeTest(cap, pairs, tpt);
                System.out.print("Pairs: " + pairs + "\t");
                t.test();
                System.out.print("\t");
                Thread.sleep(1000);
                t.test();
                System.out.println();
                Thread.sleep(1000);
            }
        }
        pool.shutdown();
    }
}
