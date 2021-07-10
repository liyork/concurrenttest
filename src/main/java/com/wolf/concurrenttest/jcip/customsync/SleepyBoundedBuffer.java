package com.wolf.concurrenttest.jcip.customsync;

import net.jcip.annotations.ThreadSafe;

/**
 * Bounded buffer using crude blocking
 * 将调用方相同的pool and sleep重置机制封装到put/take中，将前置条件管理封装并提供简单对外操作方式
 * caller need not deal with the mechanics fo failure and retry.
 * 不过单纯的等待可能会有大的延迟不能及时响应
 * 不过也需要调用者处理interruptedException
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
        while (true) {
            synchronized (this) {// 获取锁
                if (!isFull()) {
                    doPut(v);
                    return;
                }
            }// 条件不满足，释放锁
            Thread.sleep(SLEEP_GRANULARITY);//同步代码块外进行休眠，避免同步中长期持有锁。
        }
    }

    public V take() throws InterruptedException {
        while (true) {
            synchronized (this) {
                if (!isEmpty()) return doTake();
            }
            Thread.sleep(SLEEP_GRANULARITY);
        }
    }
}
