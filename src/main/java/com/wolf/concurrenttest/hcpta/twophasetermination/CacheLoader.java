package com.wolf.concurrenttest.hcpta.twophasetermination;

/**
 * Description:
 * Created on 2021/9/26 9:06 AM
 *
 * @author 李超
 * @version 0.0.1
 */
@FunctionalInterface
public interface CacheLoader<K, V> {
    V load(K k);
}
