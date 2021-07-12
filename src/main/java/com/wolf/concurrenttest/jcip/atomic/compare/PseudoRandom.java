package com.wolf.concurrenttest.jcip.atomic.compare;

/**
 * PseudoRandom
 */
public class PseudoRandom {
    int calculateNext(int prev) {
        prev ^= prev << 6;
        prev ^= prev >>> 21;
        prev ^= (prev << 7);
        return prev;
    }
}