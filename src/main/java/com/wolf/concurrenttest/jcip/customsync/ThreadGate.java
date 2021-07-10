package com.wolf.concurrenttest.jcip.customsync;

import net.jcip.annotations.*;

/**
 * Recloseable gate using wait and notifyAll，需求变更导致的更新
 * 需求是：都达到后开启，紧接着很快的关后再开。
 * 初始时用isOpen控制开关，后来有新需求，需要添加generation
 */
@ThreadSafe
public class ThreadGate {
    // CONDITION-PREDICATE: opened-since(n) (isOpen || generation>n)
    @GuardedBy("this")
    private boolean isOpen;
    @GuardedBy("this")
    // every time the gate is closed a "generation" counter is incremented, and a thread may pass await if the gate is
    // open now or if the gate has opened since this thread arrived at the gate，现在开的或者到达时已经开的(generation一样)
    private int generation;

    public synchronized void close() {
        isOpen = false;
    }

    public synchronized void open() {
        ++generation;
        isOpen = true;
        notifyAll();
    }

    // BLOCKS-UNTIL: opened-since(generation on entry)
    public synchronized void await() throws InterruptedException {
        int arrivalGeneration = generation;
        while (!isOpen && arrivalGeneration == generation) {
            wait();
        }
    }
}
