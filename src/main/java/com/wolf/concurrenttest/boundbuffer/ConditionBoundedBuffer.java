package com.wolf.concurrenttest.boundbuffer;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ConditionBoundedBuffer
 *
 * 同一时间只有一个线程执行，
 * put操作直到满才会等待,直到isFull被唤醒，每次放入都唤醒一个isEmpty
 * take操作直到空才会等待,直到isEmpty被唤醒，每次拿取都唤醒一个isFull
 * 不会产生信号丢失，因为put都等在了isFull,而take都等在了isEmpty，put每次唤醒一个isEmpty，take每次唤醒一个sFull
 * 条件队列谓词单一，所以唤醒一个就用了不会丢失，而这个还能唤醒反方向的。
 *
 * <p/>
 * Bounded buffer using explicit condition variables
 * 将多个谓词放在多个条件队列中
 * 和BoundedBuffer具有相同语义，因为都是整体上锁，然后内部判断满了就等，空了就等，否则放或者取
 * 想想能不能用读写锁改写这个。读-读没有任何阻塞，不需要上锁。应该可以
 *
 * @author Brian Goetz and Tim Peierls
 */

@ThreadSafe
public class ConditionBoundedBuffer<T> {
    protected final Lock lock = new ReentrantLock();
    // CONDITION PREDICATE: isFull (count < items.length)
    private final Condition isFull = lock.newCondition();
    // CONDITION PREDICATE: isEmpty (count > 0)
    private final Condition isEmpty = lock.newCondition();
    private static final int BUFFER_SIZE = 100;
    @GuardedBy("lock")
    private final T[] items = (T[]) new Object[BUFFER_SIZE];
    @GuardedBy("lock")
    private int tail, head, count;

    // BLOCKS-UNTIL: isFull
    public void put(T x) throws InterruptedException {
        lock.lock();
        try {
            while(count == items.length) isFull.await();
            items[tail] = x;
            if(++tail == items.length) tail = 0;
            ++count;
            isEmpty.signal();//因为只有一个线程进和出，减少上下文切换。
        } finally {
            lock.unlock();
        }
    }

    // BLOCKS-UNTIL: isEmpty
    public T take() throws InterruptedException {
        lock.lock();
        try {
            while(count == 0) isEmpty.await();
            T x = items[head];
            items[head] = null;
            if(++head == items.length) head = 0;
            --count;
            isFull.signal();
            return x;
        } finally {
            lock.unlock();
        }
    }
}
