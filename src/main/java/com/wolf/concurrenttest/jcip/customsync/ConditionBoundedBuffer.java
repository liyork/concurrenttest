package com.wolf.concurrenttest.jcip.customsync;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Bounded buffer using explicit condition variables
 * using two Conditions, notFull and notEmpty, to represent explicitly the "not full" and "not empty" condition predicates.
 * take时若是空则在notEmpty上等待，从操作线程角度考虑的两个名字
 * 将多个谓词放在多个条件队列中
 * use of condition queues is more readable - it is easier to analyze a class that uses multiple Conditions
 * Condition makes it easier to meet the requirements for single notification.
 * using the more efficient single instead of singalAll reduces the number of context switches and lock acquisitions triggered by each buffer operation
 * the three-way realationship among the lock, the condition predicate, and the condition variable must also hold when using Locks and Conditions
 * <p>
 * put操作直到满才会等待,在notFull上等待，每次放入都唤醒一个notEmpty上线程
 * take操作直到空才会等待,在notEmpty上等待，每次拿取都唤醒一个notFull上线程
 * 不会产生信号丢失，因为put都等在了notFull,而take都等在了notEmpty，put每次唤醒一个notEmpty，take每次唤醒一个notFull
 * 条件队列谓词单一，所以唤醒一个就用了不会丢失，而这个还能唤醒反方向的。
 * 想想能不能用读写锁改写这个。读-读没有任何阻塞，不需要上锁。应该可以
 */
@ThreadSafe
public class ConditionBoundedBuffer<T> {
    protected final Lock lock = new ReentrantLock();
    // CONDITION PREDICATE: notFull (count < items.length)
    private final Condition notFull = lock.newCondition();
    // CONDITION PREDICATE: notEmpty (count > 0)
    private final Condition notEmpty = lock.newCondition();
    private static final int BUFFER_SIZE = 100;
    @GuardedBy("lock")
    private final T[] items = (T[]) new Object[BUFFER_SIZE];
    @GuardedBy("lock")
    private int tail, head, count;

    // BLOCKS-UNTIL: notFull
    public void put(T x) throws InterruptedException {
        lock.lock();
        try {
            while (count == items.length) {
                notFull.await();
            }
            items[tail] = x;
            if (++tail == items.length) {
                tail = 0;
            }
            ++count;
            notEmpty.signal();//因为只有一个线程进和出，所以用signal，减少上下文切换。
        } finally {
            lock.unlock();
        }
    }

    // BLOCKS-UNTIL: isEmpty
    public T take() throws InterruptedException {
        lock.lock();
        try {
            while (count == 0) {
                notEmpty.await();
            }
            T x = items[head];
            items[head] = null;
            if (++head == items.length) {
                head = 0;
            }
            --count;
            notFull.signal();
            return x;
        } finally {
            lock.unlock();
        }
    }
}
