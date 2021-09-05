package com.wolf.concurrenttest.bfbczm.lock;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;

/**
 * Description: 先进先出锁，只有队列首元素可以获取锁，测试park/unpark
 * Created on 2021/9/5 3:39 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class FIFOMutex {
    private final AtomicBoolean locked = new AtomicBoolean(false);
    private final Queue<Thread> waiters = new ConcurrentLinkedQueue<>();

    public void lock() {
        boolean wasInterrupted = false;
        Thread current = Thread.currentThread();
        waiters.add(current);

        // 只有队首的线程可以获取锁
        while (waiters.peek() != current  // 若当前线程不是头 或 cas设定lock失败，则继续循环
                || !locked.compareAndSet(false, true)) {
            LockSupport.park(this);
            if (Thread.interrupted()) {  // 检测并重置中断标志，
                wasInterrupted = true;  // 做个标记
            }
        }

        // 已是首元 且 cas成功，则删除队头
        waiters.remove();

        // 若之前被中断过，则重设中断标记
        if (wasInterrupted) {
            current.interrupt();
        }
    }

    public void unlock() {
        // 队首元素操作，设定false
        locked.set(false);
        // 唤醒头元素
        LockSupport.unpark(waiters.peek());
    }
}
