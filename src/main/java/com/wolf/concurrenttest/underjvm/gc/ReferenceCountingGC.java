package com.wolf.concurrenttest.underjvm.gc;

/**
 * Description: 演示jvm对于循环引用是否进行清理，可见已进行回收，使用的是引用可达性进行gc
 * Created on 2021/7/17 3:56 PM
 * javac com/wolf/concurrenttest/underjvm/gc/ReferenceCountingGC.java
 * java -XX:+PrintGCDetails com.wolf.concurrenttest.underjvm.gc.ReferenceCountingGC
 *
 * @author 李超
 * @version 0.0.1
 */
public class ReferenceCountingGC {
    public Object instance = null;
    private static final int _1MB = 1024 * 1024;

    // 占用内存，以便gc日志查看是否回收过
    private byte[] bigSize = new byte[2 * _1MB];

    public static void main(String[] args) throws InterruptedException {
        ReferenceCountingGC objA = new ReferenceCountingGC();
        ReferenceCountingGC objB = new ReferenceCountingGC();
        objA.instance = objB;
        objB.instance = objA;

        objA = null;
        objB = null;

        System.gc();
        Thread.sleep(5000);
    }
}
