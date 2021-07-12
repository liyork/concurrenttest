package com.wolf.concurrenttest.jcip.atomic;

import net.jcip.annotations.ThreadSafe;

/**
 * Description: Nonblocking counter using CAS
 * 封装方法，供外界使用，屏蔽外界使用的门槛
 *
 * @author 李超
 * @date 2019/02/15
 */
@ThreadSafe
public class CasCounter {
    private SimulatedCAS value;

    public int getValue() {
        return value.get();
    }

    // follows the canonical form - fetch the old value, transform it to the new value, adn use CAS to set the nwe value
    // Retrying repeatedly is usually a reasonable strategy, although in cases of extreme contention it might be desirable
    // to wait or back of before retrying to avoid livelock
    public int increment() {
        int v;
        do {
            v = value.get();
        } while (v != value.compareAndSwap(v, v + 1));
        return v + 1;
    }
}
