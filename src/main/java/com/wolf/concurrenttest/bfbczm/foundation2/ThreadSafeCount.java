package com.wolf.concurrenttest.bfbczm.foundation2;

/**
 * Description: 用锁保证原子性
 * Created on 2021/9/3 10:22 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ThreadSafeCount {
    private Long value;

    public synchronized Long getValue() {
        return value;
    }

    public synchronized void inc() {
        ++value;
    }
}
