package com.wolf.concurrenttest.boundbuffer;

import net.jcip.annotations.*;

/**
 * BoundedBuffer
 * <p/>
 * Bounded buffer using condition queues
 * 使用wati和notify判断满/空则阻塞,避免太多的忙等与休眠。
 *
 * @author Brian Goetz and Tim Peierls
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
        while(isFull()) {
            wait();//每次醒来都得重新获取锁并判断下条件
        }
        doPut(v);
        notifyAll();//后面如果有代码要尽快执行，要不然wait无法获取锁
    }

    // BLOCKS-UNTIL: not-empty
    public synchronized V take() throws InterruptedException {
        while(isEmpty()) {
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
        while(isFull()) wait();
        boolean wasEmpty = isEmpty();//适当优化，套件通知，如果为空则通知，否则本身就满了，就不通知了，没太理解。。？？
        doPut(v);
        if(wasEmpty) notifyAll();
    }
}
