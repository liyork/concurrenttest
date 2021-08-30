package com.wolf.concurrenttest.taojcp.lock;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Description: 有边界的队列，展示Condition用法
 * Created on 2021/8/29 3:57 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class BoundedQueue<T> {
    private Object[] items;
    // 即将被添加的下标
    private int addIndex;
    // 即将被删除的下标
    private int removeIndex;
    // 数组当前数量
    private int count;
    private Lock lock = new ReentrantLock();
    // 不为空，获取时若为空则阻塞在这个条件上，放入时会进行通知
    private Condition notEmpty = lock.newCondition();
    // 不为满，放入时若满则阻塞，取出时则通知
    private Condition notFull = lock.newCondition();

    public BoundedQueue(int size) {
        items = new Object[size];
    }

    // 添加元素，若满，则添加线程进入等待状态，直到有"空位"
    public void add(T t) throws InterruptedException {
        lock.lock();  // 获取锁，确保数组修改的可见性和排他性
        try {
            while (count == items.length) {  // 用while而非if，目的是防止过早或意外的通知，只有条件符合才退出循环
                notFull.await();  // 释放锁并进入等待状态
            }
            items[addIndex] = t;
            if (++addIndex == items.length) {
                addIndex = 0;
            }
            ++count;
            notEmpty.signal();  // 通知等待在notEmpty上的线程，数组中已经有了新元素可以获取
        } finally {
            lock.unlock();
        }
    }

    // 由头部删除，若数组空，则删除线程进入等待状态，直到有新添加元素
    public T remove() throws InterruptedException {
        lock.lock();
        try {
            while (count == 0) {
                notEmpty.await();
            }
            Object x = items[removeIndex];
            if (++removeIndex == items.length) {
                removeIndex = 0;
            }
            --count;
            notFull.signal();
            return (T) x;
        } finally {
            lock.unlock();
        }
    }
}
