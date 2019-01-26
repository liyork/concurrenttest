package com.wolf.concurrenttest.optimize.numberrange;

import java.util.concurrent.atomic.*;

/**
 * NumberRange
 * <p/>
 * Number range class that does not sufficiently protect its invariants
 *
 * 有可能原来是0-10，现在同时调用setLower和setUpper使用参数5 - 3,由于未加锁保证两者顺序，则导致最终5-3错误
 * CasNumberRange对这个进行了优化
 *
 * @author Brian Goetz and Tim Peierls
 */

public class NumberRange {
    // INVARIANT: lower <= upper
    private final AtomicInteger lower = new AtomicInteger(0);
    private final AtomicInteger upper = new AtomicInteger(0);

    public void setLower(int i) {
        // Warning -- unsafe check-then-act
        if(i > upper.get()) throw new IllegalArgumentException("can't set lower to " + i + " > upper");
        lower.set(i);
    }

    public void setUpper(int i) {
        // Warning -- unsafe check-then-act
        if(i < lower.get()) throw new IllegalArgumentException("can't set upper to " + i + " < lower");
        upper.set(i);
    }

    public boolean isInRange(int i) {
        return (i >= lower.get() && i <= upper.get());
    }
}

