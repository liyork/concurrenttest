package com.wolf.concurrenttest.bfbczm.lock;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Description: 用ReentrantLock保证list线程安全
 * Created on 2021/9/6 1:05 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ReentrantLockList {
    private ArrayList<String> array = new ArrayList<>();
    private volatile ReentrantLock lock = new ReentrantLock();

    public void add(String e) {
        lock.lock();
        try {
            array.add(e);
        } finally {
            lock.unlock();
        }
    }

    public void remove(String e) {
        lock.lock();
        try {
            array.remove(e);
        } finally {
            lock.unlock();
        }
    }

    public String get(int index) {
        lock.lock();
        try {
            return array.get(index);
        } finally {
            lock.unlock();
        }
    }
}
