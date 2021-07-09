package com.wolf.concurrenttest.jcip.testing;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import java.util.concurrent.Semaphore;

/**
 * Bounded buffer using Semaphore
 * 单纯put和remove测试：LinkedBlockingQueue 性能高于 ArrayBlockingQueue 高于SemaphoreBoundedBuffer
 */
@ThreadSafe
public class SemaphoreBoundedBuffer<E> {
    // the number of elements that can be removed from the buffer
    // 有多少元素可以取
    private final Semaphore availableItems;
    // how many items can be inserted into the buffer
    // 有多少空间可以放
    private final Semaphore availableSpaces;
    @GuardedBy("this")
    private final E[] items;
    @GuardedBy("this")
    private int putPosition = 0;
    private int takePosition = 0;

    public SemaphoreBoundedBuffer(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException();
        availableItems = new Semaphore(0);
        availableSpaces = new Semaphore(capacity);
        items = (E[]) new Object[capacity];
    }

    // 是否为空，表示是否有人放入过，若有则put后availableItems会增加
    public boolean isEmpty() {
        return availableItems.availablePermits() == 0;
    }

    // 是否满，每次放入则availableSpaces--，若0则表示满了
    public boolean isFull() {
        return availableSpaces.availablePermits() == 0;
    }

    //availableSpaces可以控制最大放入的数量，再放入就要是阻塞，同时可以允许插入的线程数量，不过里面还是得阻塞插入，
    // 相对于一开始就锁的插入效率会提升些
    public void put(E x) throws InterruptedException {
        //有空间就能放
        availableSpaces.acquire();
        doInsert(x);
        availableItems.release();
    }

    public E take() throws InterruptedException {
        //有东西就能取
        availableItems.acquire();
        E item = doExtract();
        availableSpaces.release();
        return item;
    }

    private synchronized void doInsert(E x) {
        int i = putPosition;
        items[i] = x;
        putPosition = (++i == items.length) ? 0 : i;
    }

    private synchronized E doExtract() {
        int i = takePosition;
        E x = items[i];
        items[i] = null;//gc
        takePosition = (++i == items.length) ? 0 : i;
        return x;
    }
}
