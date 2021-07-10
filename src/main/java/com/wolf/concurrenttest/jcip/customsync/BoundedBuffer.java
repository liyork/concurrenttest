package com.wolf.concurrenttest.jcip.customsync;

import net.jcip.annotations.ThreadSafe;

/**
 * Bounded buffer using condition queues
 * 使用wait和notify判断满/空则阻塞,避免太多的忙等与休眠。
 * a condition queue gets its name because it gives a group of threads - called the wait set - a
 * way to wait for a specific condition to become true. 元素是线程等待这个条件
 * each object can ack lock/condition queue, the wait/notify/notifyAll constitute the API for intrinsic condition queues
 * 先持有锁才能用条件队列的方法
 * it is easy to use and manages state dependence sensibly
 */
@ThreadSafe
public class BoundedBuffer<V> extends BaseBoundedBuffer<V> {
    // CONDITION PREDICATE: not-full (!isFull())
    // CONDITION PREDICATE: not-empty (!isEmpty())
    public BoundedBuffer() {
        this(100);
    }

    public BoundedBuffer(int size) {
        super(size);
    }

    // BLOCKS-UNTIL: not-full
    public synchronized void put(V v) throws InterruptedException {
        //由于醒来时还需要持有一遍锁并且有可能条件并不满足，则可能继续等待
        while (isFull()) {
            wait();//每次醒来都得重新获取锁并判断下条件
        }
        doPut(v);
        notifyAll();//后面如果有代码要尽快执行，要不然wait无法获取锁
    }

    // using conditional notification
    // a thread can be released from a wait only if the buffer goes from empty to not empty or from full to not full
    public synchronized void put2(V v) throws InterruptedException {
        while (isFull()) {
            wait();
        }
        boolean wasEmpty = isEmpty();
        doPut(v);
        if (wasEmpty) {
            notifyAll();
        }
    }

    // BLOCKS-UNTIL: not-empty
    public synchronized V take() throws InterruptedException {
        while (isEmpty()) {
            wait();
        }
        V v = doTake();
        notifyAll();//条件队列中包含基于两种谓词的判断，空或者满，如果用notify，有可能唤醒了未成真的线程，然后继续等待，就一直都等待了。。
        //所有谓词都一样时，才可以用notify
        return v;
    }

    // BLOCKS-UNTIL: not-full
    // Alternate form of put() using conditional notification
    public synchronized void alternatePut(V v) throws InterruptedException {
        while (isFull()) wait();
        boolean wasEmpty = isEmpty();//适当优化，套件通知，如果为空则通知，否则本身就满了，就不通知了，没太理解。。？？
        doPut(v);
        if (wasEmpty) notifyAll();
    }
}
