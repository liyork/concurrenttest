package com.wolf.concurrenttest.jcip;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

/**
 * Description: 为保证可见性，需要用同步
 * Created on 2021/6/26 4:32 PM
 *
 * @author 李超
 * @version 0.0.1
 */
@ThreadSafe
public class SychronizedInteger {
    @GuardedBy("this")
    private int value;

    public synchronized int get() {
        return value;
    }

    public synchronized void set(int value) {
        this.value = value;
    }
}
