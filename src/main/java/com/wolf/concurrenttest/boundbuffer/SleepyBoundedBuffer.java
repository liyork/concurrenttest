package com.wolf.concurrenttest.boundbuffer;

import net.jcip.annotations.*;

/**
 * SleepyBoundedBuffer
 * <p/>
 * Bounded buffer using crude blocking
 * 带有等待重试机制，将前提条件管理封装起来，简化客户端对缓存使用
 *
 * @author Brian Goetz and Tim Peierls
 */
@ThreadSafe
public class SleepyBoundedBuffer<V> extends BaseBoundedBuffer<V> {
    int SLEEP_GRANULARITY = 60;

    public SleepyBoundedBuffer() {
        this(100);
    }

    public SleepyBoundedBuffer(int size) {
        super(size);
    }

    public void put(V v) throws InterruptedException {
        while(true) {
            synchronized(this) {
                if(!isFull()) {
                    doPut(v);
                    return;
                }
            }
            Thread.sleep(SLEEP_GRANULARITY);//同步外休眠，避免同步中长期持有锁。
        }
    }

    public V take() throws InterruptedException {
        while(true) {
            synchronized(this) {
                if(!isEmpty()) return doTake();
            }
            Thread.sleep(SLEEP_GRANULARITY);
        }
    }
}
