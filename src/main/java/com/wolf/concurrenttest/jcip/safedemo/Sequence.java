package com.wolf.concurrenttest.jcip.safedemo;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

/**
 * Description: 展示简单并发问题解决方式-同步
 * Created on 2021/6/26 8:32 AM
 *
 * @author 李超
 * @version 0.0.1
 */
@ThreadSafe
public class Sequence {
    @GuardedBy("this")
    private int nextValue;

    public synchronized int getNext() {
        return nextValue++;
    }
}
