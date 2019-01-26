package com.wolf.concurrenttest.lock;

import java.util.concurrent.atomic.AtomicReference;

/**
 * 简单实现
 * 缺点：CAS操作需要硬件的配合；
 * 保证各个CPU的缓存（L1、L2、L3、跨CPU Socket、主存）的数据一致性，通讯开销很大，在多处理器系统上更严重；
 * 没法保证公平性，不保证等待进程/线程按照FIFO顺序获得锁。
 */
public class SimpleSpinLock {
    private volatile AtomicReference<Thread> owner = new AtomicReference<>();
    private volatile int count = 0;

    public void lock() {
        Thread current = Thread.currentThread();
        if(current == owner.get()) {
            count++;
            return;
        }
        //竞争者不停地调用费时的原子操作
        while(!owner.compareAndSet(null, current)) {

        }
    }

    public void unlock() {
        Thread current = Thread.currentThread();
        if(current == owner.get()) {
            if(count != 0) {
                count--;
            } else {
                owner.compareAndSet(current, null);
            }
        }
    }
}
