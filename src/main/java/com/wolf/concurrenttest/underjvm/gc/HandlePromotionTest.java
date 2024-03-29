package com.wolf.concurrenttest.underjvm.gc;

/**
 * Description: 空间分配担保
 * javac com/wolf/concurrenttest/underjvm/gc/HandlePromotionTest.java
 * java -XX:+UseParNewGC -verbose:gc -Xms20M -Xmx20M -Xmn10M -XX:+PrintGCDetails -XX:SurvivorRatio=8 -XX:HandlePromotionFailure=true com.wolf.concurrenttest.underjvm.gc.HandlePromotionTest
 * Created on 2021/7/20 6:18 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class HandlePromotionTest {
    private static final int _1MB = 1024 * 1024;

    public static void main(String[] args) {
        byte[] allocation1, allocation2, allocation3, allocation4, allocation5, allocation6, allocation7;
        allocation1 = new byte[2 * _1MB];
        allocation2 = new byte[2 * _1MB];
        allocation3 = new byte[2 * _1MB];
        allocation1 = null;
        allocation4 = new byte[2 * _1MB];
        allocation5 = new byte[2 * _1MB];
        allocation6 = new byte[2 * _1MB];
        allocation4 = null;
        allocation5 = null;
        allocation6 = null;
        allocation7 = new byte[2 * _1MB];
    }
}
