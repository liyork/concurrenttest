package com.wolf.concurrenttest.taojcp.lock;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * Description: 同一时间只允许最多两个线程并发
 * Created on 2021/8/29 7:02 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class TwinsLock {
    private final Sync sync = new Sync(2);

    private static final class Sync extends AbstractQueuedSynchronizer {
        Sync(int count) {
            if (count <= 0) {
                throw new IllegalArgumentException("count must large than zero.");
            }
            setState(count);
        }

        @Override
        protected int tryAcquireShared(int reduceCount) {
            for (; ; ) {
                int current = getState();
                int newCount = current - reduceCount;
                // 小于0直接返回，否则(>=0)尝试cas若成功则返回。
                if (newCount < 0 || compareAndSetState(current, newCount)) {
                    return newCount;
                }
            }
        }

        @Override
        protected boolean tryReleaseShared(int returnCount) {
            for (; ; ) {
                int current = getState();
                int newCount = current + returnCount;
                if (compareAndSetState(current, newCount)) {
                    return true;
                }
            }
        }
    }

    public void lock() {
        sync.acquireShared(1);
    }

    public void unlock() {
        sync.releaseShared(1);
    }
}
