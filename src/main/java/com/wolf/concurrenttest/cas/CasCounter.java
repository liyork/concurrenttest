package com.wolf.concurrenttest.cas;

import net.jcip.annotations.*;

/**
 * CasCounter
 * <p/>
 * Nonblocking counter using CAS
 *
 * @author Brian Goetz and Tim Peierls
 */
@ThreadSafe
public class CasCounter {
    private SimulatedCAS value;

    public int getValue() {
        return value.get();
    }

    public int increment() {
        int v;
        do {
            v = value.get();
        } while(v != value.compareAndSwap(v, v + 1));//很激烈时最好等待一会，最好等待时间不一样,不然容易产生活锁
        return v + 1;
    }
}
