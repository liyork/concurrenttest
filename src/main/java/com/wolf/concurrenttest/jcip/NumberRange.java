package com.wolf.concurrenttest.jcip;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Description: 演示问题：多原子变量状态有关联，不能分别交给每个底层原子类保证统一的安全
 * does Not Sufficiently Protect Its Invariants
 * 例如：当前(0,10)，若是threadA设定lower=5,满足但是还没有执行set，而threadB设定4，也满足(因为现在lower还是10)，最后结果就是(5,4)
 * 很像volatile原则: a variable is suitable for being declared volatile only if it does not participate in invariants involving other state variables.
 * Created on 2021/6/28 6:36 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class NumberRange {
    // INVARIANT: lower <= upper
    private final AtomicInteger lower = new AtomicInteger(0);
    private final AtomicInteger upper = new AtomicInteger(0);

    public void setLower(int i) {
        // warning -- unsafe check-then-act
        if (i > upper.get()) {
            throw new IllegalArgumentException("can't set lower to " + i + " > upper");
        }
        lower.set(i);
    }

    public void setUpper(int i) {
        // warning -- unsafe check-then-act
        if (i < lower.get()) {
            throw new IllegalArgumentException("can't set upper to " + i + " < lower");
        }
        upper.set(i);
    }

    public boolean isInRange(int i) {
        return (i >= lower.get() && i <= upper.get());
    }
}
