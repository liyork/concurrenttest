package com.wolf.concurrenttest.underjvm.gc;

/**
 * Description: 测试新生代Minor GC
 * javac com/wolf/concurrenttest/underjvm/gc/GCTest.java
 * java -verbose:gc -Xms20M -Xmx20M -Xmn10M -XX:+PrintGCDetails -XX:SurvivorRatio=8 com.wolf.concurrenttest.underjvm.gc.GCTest
 * 讲解：
 * java堆20M，10M给新生代，剩余的10M给老年代。Eden与一个Survivor8:1。新生代总可用空间(Eden+1个Survivor)
 * 当分配4M时发现Eden已经占用了6M，剩余空间不足以分配，产生MinorGC，垃圾收集期间又发现3个2M的对象无法全部放入Survivor(Survivor只有1M)，只好通过
 * 分配担保机制提前转移到老年代。收集结束后，4M放入Eden，Survivor空闲，老年代占用6M(被allocation1、2、3占用)
 * Created on 2021/7/20 9:27 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class AllocationTest {
    private static final int _1MB = 1024 * 1024;

    public static void main(String[] args) {
        byte[] allocation1, allocation2, allocation3, allocation4;
        allocation1 = new byte[2 * _1MB];
        allocation2 = new byte[2 * _1MB];
        allocation3 = new byte[2 * _1MB];
        allocation4 = new byte[4 * _1MB];  // 出现一次Minor GC
    }
}
