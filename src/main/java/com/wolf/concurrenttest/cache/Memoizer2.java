package com.wolf.concurrenttest.cache;

import java.util.*;
import java.util.concurrent.*;

/**
 * Memoizer2
 * <p/>
 * Replacing HashMap with ConcurrentHashMap
 *
 * @author Brian Goetz and Tim Peierls
 */
public class Memoizer2<A, V> implements Computable<A, V> {
    private final Map<A, V> cache = new ConcurrentHashMap<A, V>();
    private final Computable<A, V> c;

    public Memoizer2(Computable<A, V> c) {
        this.c = c;
    }

    public V compute(A arg) throws InterruptedException {
        V result = cache.get(arg);//使用ConcurrentHashMap提高了并发量
        if(result == null) {
            result = c.compute(arg);//可能造成重复计算,当前线程不知道以前是否有人计算过
            cache.put(arg, result);
        }
        return result;
    }
}
