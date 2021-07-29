package com.wolf.concurrenttest.underjvm.gc;

/**
 * Description: 测试长期存活的对象进入老年代
 * javac com/wolf/concurrenttest/underjvm/gc/TenuringThresholdTest.java
 * java -XX:TargetSurvivorRatio=90 -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -verbose:gc -Xms20M -Xmx20M -Xmn10M -XX:+PrintGCDetails -XX:SurvivorRatio=8 -XX:MaxTenuringThreshold=1 -XX:+PrintTenuringDistribution com.wolf.concurrenttest.underjvm.gc.TenuringThresholdTest
 * TargetSurvivorRatio:如果Survivor空间的占用超过该设定值，对象在未达到他们的最大年龄之前就会被提升至老年代
 * 讲解：分别用1和15
 * 当MaxTenuringThreshold=1时，allocation1对象在第二次GC时进入老年代，新生代已使用的内存在垃圾收集后变成0KB。
 * 当MaxTenuringThreshold=15时，第二次GC后，allocation1对象还留在新生代Survivor空间，这时新生代仍有404KB占用
 * Created on 2021/7/20 9:27 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class TenuringThresholdTest {
    private static final int _1MB = 1024 * 1024;

    public static void main(String[] args) {
        byte[] allocation1, allocation2, allocation3;
        allocation1 = new byte[_1MB / 4];  // 什么时候进入老年代决定于XX:MaxTenuringThreshold

        // 为了产生Minor GC
        allocation2 = new byte[4 * _1MB];
        allocation3 = new byte[4 * _1MB];
        allocation3 = null;
        allocation3 = new byte[4 * _1MB];

    }
}
