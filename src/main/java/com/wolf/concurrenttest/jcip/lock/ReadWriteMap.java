package com.wolf.concurrenttest.jcip.lock;

import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Description: wrapping a Map with a Read-write Lock
 * 用ConcurrentHashMap更好，but this technique would be useful if you want to provide more concurrent access
 * to an alternate Map implementation such as LinkedHashMap
 * Created on 2021/7/10 8:05 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ReadWriteMap<K, V> {
    private final Map<K, V> map;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock r = lock.readLock();
    private final Lock w = lock.writeLock();

    public ReadWriteMap(Map<K, V> map) {
        this.map = map;
    }

    public V put(K key, V value) {
        w.lock();
        try {
            return map.put(key, value);
        } finally {
            w.unlock();
        }
    }

    public V get(Object key) {
        r.lock();
        try {
            return map.get(key);
        } finally {
            r.unlock();
        }
    }

}
