package com.wolf.concurrenttest.jcip.performancescalability;

import net.jcip.annotations.ThreadSafe;

/**
 * Description: 模拟concurrenthashmap, Striping lock demo
 * Created on 2021/7/8 6:55 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class StripingLock {
    @ThreadSafe
    class StripedMap {
        // synchronization poli cy:buckets[n] guarded by locks[n%N_LOCKS]
        private static final int N_LOCKS = 16;
        private final Node[] buckets;
        private final Object[] locks;

        public StripedMap(int numBuckets) {
            buckets = new Node[numBuckets];
            locks = new Object[N_LOCKS];
            for (int i = 0; i < N_LOCKS; i++) {
                locks[i] = new Object();
            }
        }

        private final int hash(Object key) {
            return Math.abs(key.hashCode() % buckets.length);
        }

        public Object get(Object key) {
            int hash = hash(key);
            synchronized (locks[hash % N_LOCKS]) {
                for (Node m = buckets[hash]; m != null; m = m.next) {
                    if (m.key.equals(key)) {
                        return m.value;
                    }
                }
            }
            return null;
        }

        public void clear() {
            for (int i = 0; i < buckets.length; i++) {
                synchronized (locks[i % N_LOCKS]) {
                    buckets[i] = null;
                }
            }
        }


        private class Node {
            public Node next;
            public Object key;
            public Object value;
        }
    }
}
