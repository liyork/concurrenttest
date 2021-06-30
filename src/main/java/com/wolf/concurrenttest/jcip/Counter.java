package com.wolf.concurrenttest.jcip;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

/**
 * Description: using java Monitor Pattern，上锁或者用内置锁
 * 字段少则要维护的状态少
 * Created on 2021/6/28 12:50 PM
 *
 * @author 李超
 * @version 0.0.1
 */
@ThreadSafe
public class Counter {
    @GuardedBy("this")
    private long value = 0;

    public synchronized long getValue() {
        return value;
    }

    public synchronized long increment() {
        if (value == Long.MAX_VALUE) {
            throw new IllegalStateException("counter overflow");
        }
        return ++value;
    }
}
