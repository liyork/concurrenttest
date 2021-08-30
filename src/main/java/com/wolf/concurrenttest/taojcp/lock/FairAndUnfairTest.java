package com.wolf.concurrenttest.taojcp.lock;

import com.wolf.concurrenttest.taojcp.thread.SleepUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Description: 测试公平与非公平
 * 不过应该更好的方式是，随机不断加入竞争，这样就会产生不是按照FIFO队列的内容顺序了，不过这里for2次足够了
 * Created on 2021/8/29 10:50 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class FairAndUnfairTest {
    private static ReentrantLock2 fairLock = new ReentrantLock2(true);
    private static ReentrantLock2 unfairLock = new ReentrantLock2(false);

    public static void main(String[] args) {
        FairAndUnfairTest test = new FairAndUnfairTest();

        test.testLock(fairLock);
        //test.testLock(unfairLock);
    }

    private void testLock(ReentrantLock2 lock) {
        for (int i = 0; i < 5; i++) {
            new Job(lock).start();
        }
    }

    private static class Job extends Thread {
        private ReentrantLock2 lock;

        public Job(ReentrantLock2 lock) {
            this.lock = lock;
        }

        @Override
        public void run() {
            for (int i = 0; i < 2; i++) {
                lock.lock();
                try {
                    int x = new Random().nextInt(2);
                    SleepUtils.second(x);
                    System.out.println(Thread.currentThread().getName() + "   " + lock.getQueuedThreads());
                } finally {
                    lock.unlock();
                }
            }
        }
    }

    private static class ReentrantLock2 extends ReentrantLock {
        public ReentrantLock2(boolean fair) {
            super(fair);
        }

        @Override
        protected Collection<Thread> getQueuedThreads() {
            ArrayList<Thread> arrayList = new ArrayList<>(super.getQueuedThreads());
            Collections.reverse(arrayList);
            return arrayList;
        }
    }
}
