package com.wolf.concurrenttest.cache;

import com.wolf.concurrenttest.common.TakeTimeUtils;

import java.math.BigInteger;

/**
 * Description:
 * <br/> Created on 2017/6/25 9:53
 *
 * @author 李超
 * @since 1.0.0
 */
class ExpensiveFunction implements Computable<String, BigInteger> {
    public BigInteger compute(String arg) {
        // after deep thought...
        TakeTimeUtils.simulateLongTimeOperation(600000);
        return new BigInteger(arg);
    }
}
