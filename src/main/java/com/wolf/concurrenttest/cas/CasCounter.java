package com.wolf.concurrenttest.cas;

import net.jcip.annotations.ThreadSafe;

/**
 * Description: 封装方法，供外界使用，评比外界使用的门槛
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

    public int incrementUntilSuccess() {
        int v;
        do {
            v = value.get();
        } while (v != value.compareAndSwap(v, v + 1));//很激烈时最好等待一会，最好等待时间不一样,不然容易产生活锁
        return v + 1;
    }
}
