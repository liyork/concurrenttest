package com.wolf.concurrenttest.hcpta.twophasetermination;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Description: 双向链表+hash表实现
 * Created on 2021/9/26 8:55 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class LRUCache<K, V> {
    // 记录key的顺序，依据其进行清理策略
    private final LinkedList<K> keyList = new LinkedList<>();

    // 存放数据
    private final Map<K, V> cache = new HashMap<>();

    // cache最大容量
    private final int capacity;

    // 该接口提供一种加载数据的方式
    private final CacheLoader<K, V> cacheLoader;

    public LRUCache(int capacity, CacheLoader<K, V> cacheLoader) {
        this.capacity = capacity;
        this.cacheLoader = cacheLoader;
    }

    public void put(K key, V value) {
        // 超过容量，则清除最老数据
        if (keyList.size() >= capacity) {
            K eldestKey = keyList.removeFirst();// eldest data
            cache.remove(eldestKey);
        }
        // 已经存在，删除
        if (keyList.contains(key)) {
            keyList.remove(key);
        }

        // key入队尾
        keyList.addLast(key);
        cache.put(key, value);
    }

    public V get(K key) {
        V value;
        boolean success = keyList.remove(key);
        if (!success) {  // key中不存在
            // 重新加载
            value = cacheLoader.load(key);
            // 放入数据
            this.put(key, value);
        } else {  // 删除key成功
            value = cache.get(key);
            // 再次放入key
            keyList.addLast(key);
        }
        return value;
    }

    @Override
    public String toString() {
        return this.keyList.toString();
    }
}
