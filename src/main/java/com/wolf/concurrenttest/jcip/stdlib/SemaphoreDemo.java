package com.wolf.concurrenttest.jcip.stdlib;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Semaphore;

/**
 * Description: 演示用Semaphore实现资源的可控
 * Counting semaphores are used to control the number of activities that can access a certain resource or perform a
 * given action at the same time.
 * can be used to implement resource pools or to impose a bound on a collection.
 * Created on 2021/6/30 6:56 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class SemaphoreDemo<T> {
    private final Set<T> set;
    private final Semaphore sem;

    public SemaphoreDemo(int bound) {
        this.set = Collections.synchronizedSet(new HashSet<>());
        sem = new Semaphore(bound);
    }

    public boolean add(T o) throws InterruptedException {
        sem.acquire();
        boolean wasAdded = false;
        try {
            wasAdded = set.add(o);
            return wasAdded;
        } finally {
            if (!wasAdded) {
                sem.release();
            }
        }
    }

    public boolean remove(Object o) {
        boolean wasRemoved = set.remove(o);
        if (wasRemoved) {
            sem.release();
        }
        return wasRemoved;
    }
}
