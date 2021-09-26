package com.wolf.concurrenttest.hcpta.twophasetermination;

/**
 * Description: 基本功能验证
 * Created on 2021/9/26 9:07 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class LRUCacheTest {
    public static void main(String[] args) {
        LRUCache<String, Reference> cache = new LRUCache<>(5, key -> new Reference());
        cache.get("alex");
        cache.get("jack");
        cache.get("gavin");
        cache.get("dillon");
        cache.get("leo");

        cache.get("jenny");  // alex将被移除
        System.out.println(cache.toString());
    }
}
