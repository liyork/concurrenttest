package com.wolf.concurrenttest.common;

/**
 * Description:
 * <br/> Created on 2019-01-26
 *
 * @author 李超
 * @since 1.0.0
 */
public class TakeTimeUtils {

    public static void simulateLongTimeOperation(int maxCounter) {
        int counter = 0;
        while(counter <= maxCounter) {
            double hypot = Math.hypot(counter, counter);
            Math.atan2(hypot, counter);
            counter++;
        }
    }
}
