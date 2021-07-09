package com.wolf.concurrenttest.jcip.testing;

import org.junit.Test;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Basic unit tests for SemaphoreBoundedBuffer
 */
public class SemaphoreBoundedBufferTest {
    private static final long LOCKUP_DETECT_TIMEOUT = 1000;
    private static final int CAPACITY = 10000;
    private static final int THRESHOLD = 10000;

    @Test
    public void testIsEmptyWhenConstructed() {
        SemaphoreBoundedBuffer<Integer> bb = new SemaphoreBoundedBuffer<>(10);
        assert (bb.isEmpty());
        assert !(bb.isFull());
    }

    @Test
    public void testIsFullAfterPuts() throws InterruptedException {
        SemaphoreBoundedBuffer<Integer> bb = new SemaphoreBoundedBuffer<>(10);
        for (int i = 0; i < 10; i++)
            bb.put(i);
        assert (bb.isFull());
        assert !(bb.isEmpty());
    }

    // testing blocking an responsiveness to interruption
    //注意：Thread.State并不可靠，仅用于测试。
    @Test
    public void testTakeBlocksWhenEmpty() {
        final SemaphoreBoundedBuffer<Integer> bb = new SemaphoreBoundedBuffer<>(10);
        Thread taker = new Thread(() -> {
            try {
                int unused = bb.take();
                fail(); // if we get here, it's an error
            } catch (InterruptedException success) {
            }
        });

        try {
            taker.start();
            Thread.sleep(LOCKUP_DETECT_TIMEOUT);
            taker.interrupt(); // 主线程调用interrupt
            taker.join(LOCKUP_DETECT_TIMEOUT); // 主线程等待taker退出
            assert !(taker.isAlive());
        } catch (Exception unexpected) {

        }
    }

    private void fail() {
        throw new RuntimeException("fail...");
    }

    // 当producer速度快于consumer则内存不断增长，引起producer阻塞将消费更多的内存和资源
    // 可以用heap-profiling tools测试
    // testing for resource leaks
    class Big {
        double[] data = new double[100000];
    }

    void testLeak() throws InterruptedException {
        SemaphoreBoundedBuffer<Big> bb = new SemaphoreBoundedBuffer<>(CAPACITY);
        int heapSize1 = snapshotHeap();
        for (int i = 0; i < CAPACITY; i++)
            bb.put(new Big());
        for (int i = 0; i < CAPACITY; i++)
            bb.take();
        int heapSize2 = snapshotHeap();
        assert (Math.abs(heapSize1 - heapSize2) < THRESHOLD);
    }

    // Snapshot heap and return heap size
    private int snapshotHeap() {
        return 0;
    }


    private final TestingThreadFactory threadFactory = new TestingThreadFactory();

    // verify thread pool Expansion
    @Test
    public void testPoolExpansion() throws InterruptedException {
        int MAX_SIZE = 10;
        ExecutorService exec = Executors.newFixedThreadPool(MAX_SIZE);

        for (int i = 0; i < 10 * MAX_SIZE; i++)
            exec.execute(() -> {
                try {
                    Thread.sleep(Long.MAX_VALUE);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });

        for (int i = 0; i < 20 && threadFactory.numCreated.get() < MAX_SIZE; i++) {
            Thread.sleep(100);
        }
        assert (threadFactory.numCreated.get() == MAX_SIZE);
        exec.shutdownNow();
    }

    // thread factory for testing ThreadPoolExecutor
    static class TestingThreadFactory implements ThreadFactory {
        public final AtomicInteger numCreated = new AtomicInteger(); // 对测试暴露
        private final ThreadFactory factory = Executors.defaultThreadFactory();

        public Thread newThread(Runnable r) {
            numCreated.incrementAndGet();
            return factory.newThread(r);
        }
    }

    Random random = new Random();

    // using Thread.yield to generate more interleavings.
    public synchronized void transferCredits(Account from, Account to, int amount) {
        from.setBalance(from.getBalance() - amount);
        if (random.nextInt(100) > 1000) {
            Thread.yield();
        }
        to.setBalance(to.getBalance() + amount);
    }

    class Account {
        public void setBalance(int amount) {

        }

        public int getBalance() {
            return 0;
        }
    }
}
