package com.wolf.concurrenttest.underjvm.gc;

/**
 * Description: 测试动态对象年龄判定
 * javac com/wolf/concurrenttest/underjvm/gc/TenuringThreshold2Test.java
 * java -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -verbose:gc -Xms20M -Xmx20M -Xmn10M -XX:+PrintGCDetails -XX:SurvivorRatio=8 -XX:MaxTenuringThreshold=15 -XX:+PrintTenuringDistribution com.wolf.concurrenttest.underjvm.gc.TenuringThreshold2Test
 * 讲解：
 * 发现运行中Survivor占用仍然为0%，老年代比预期增加了6%，即allocation1、allocation2对象都直接进入了老年代，并没有等到15岁的临界年龄。
 * 因为这俩加起来已经达到512KB(Survivor容量/2)，并且他们都是同龄的，满足同年对象达到Survivor空间一半规则。
 * 只要注释掉其中一个对象的new操作，就会发现另一个就不会晋升到老年代了。
 * Created on 2021/7/20 9:27 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class TenuringThreshold2Test {
    private static final int _1MB = 1024 * 1024;

    public static void main(String[] args) {
        byte[] allocation1, allocation2, allocation3, allocation4;
        allocation1 = new byte[_1MB / 4];  // allocation1+allocation2大于survivor空间一半
        //allocation2 = new byte[_1MB / 4];

        allocation3 = new byte[4 * _1MB];
        allocation4 = new byte[4 * _1MB];
        allocation4 = null;
        allocation4 = new byte[4 * _1MB];
    }
}
