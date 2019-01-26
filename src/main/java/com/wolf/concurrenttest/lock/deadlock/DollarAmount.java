package com.wolf.concurrenttest.lock.deadlock;

/**
 * Description:
 * <br/> Created on 2017/6/26 15:51
 *
 * @author 李超
 * @since 1.0.0
 */
public class DollarAmount implements Comparable<DollarAmount> {

    private int dollars;

    public int compareTo(DollarAmount other) {
        return 0;
    }

    DollarAmount(int dollars) {
        this.dollars = dollars;
    }
}
