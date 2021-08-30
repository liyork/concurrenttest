package com.wolf.concurrenttest.taojcp.lock;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Description: 读写锁展示
 * Created on 2021/8/29 11:46 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class Cache {
    static Map<String, Object> map = new HashMap<>();
    static ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    static Lock r = rwl.readLock();
    static Lock w = rwl.writeLock();

    public static final Object get(String key) {
        r.lock();  // 用读锁，使得并发访问该方法时不会被阻塞
        try {
            return map.get(key);
        } finally {
            r.unlock();
        }
    }

    // 放入并返回旧值
    public static final Object put(String key, Object value) {
        w.lock();  // 用写锁，当获取后，其他线程对于读锁和写锁的获取均会阻塞
        try {
            return map.put(key, value);
        } finally {
            w.unlock();
        }
    }

    public static final void clear() {
        w.lock();
        try {
            map.clear();
        } finally {
            w.unlock();
        }
    }
}
