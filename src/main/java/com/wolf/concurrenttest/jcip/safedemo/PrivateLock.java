package com.wolf.concurrenttest.jcip.safedemo;

import net.jcip.annotations.GuardedBy;

/**
 * Description: Guarding State with a Private Lock
 * Java monitor pattern
 * Created on 2021/6/28 1:26 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class PrivateLock {
    private final Object myLock = new Object();
    @GuardedBy("myLock")
    Widget widget;

    void someMethod() {
        synchronized (myLock) {
            // access or modify the state of widget
        }
    }

    private class Widget {
    }
}
