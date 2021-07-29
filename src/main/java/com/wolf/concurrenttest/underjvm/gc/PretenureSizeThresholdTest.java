package com.wolf.concurrenttest.underjvm.gc;

/**
 * Description: 测试大对象直接进入老年代
 * javac com/wolf/concurrenttest/underjvm/gc/PretenureSizeThresholdTest.java
 * java -XX:+UseSerialGC -verbose:gc -Xms20M -Xmx20M -Xmn10M -XX:+PrintGCDetails -XX:SurvivorRatio=8 -XX:PretenureSizeThreshold=3145728 com.wolf.concurrenttest.underjvm.gc.PretenureSizeThresholdTest
 * 讲解：
 * 可以看到Eden空间几乎没有使用，而老年代10M被使用了40%，即4M的对象直接分配到了老年代，因为PretenureSizeThreshold设了3M，因此超过3MB的对象会直接在老年代中分配。
 * 注：PretenureSizeThreshold只对Serial和ParNew两款新生代收集器有效。若必须用此参数调优，考虑ParNew+CMS收集器组合
 * Created on 2021/7/20 9:27 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class PretenureSizeThresholdTest {
    private static final int _1MB = 1024 * 1024;

    public static void main(String[] args) {
        byte[] allocation;
        allocation = new byte[4 * _1MB];  // 直接分配在老年代中
    }
}
