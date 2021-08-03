package com.wolf.concurrenttest.underjvm.memorypartition;

import java.util.ArrayList;

/**
 * Description: 测试oom场景
 * -verbose:gc -Xms10m -Xmx10m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/datas/oom
 * mac上跑似乎能gc。。
 * docker上可以实现
 * javac com/wolf/concurrenttest/underjvm/HeapOOM.java
 * java -verbose:gc -Xms10m -Xmx10m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/datas/oom com.wolf.concurrenttest.underjvm.memorypartition.HeapOOM
 * Created on 2021/7/17 7:47 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class HeapOOM {
    static class OOMObject {

    }

    public static void main(String[] args) throws InterruptedException {
        //testBase();

        testCatch();
    }

    // 即使catch住，也会有文件输出
    private static void testCatch() throws InterruptedException {
        try {
            testBase();
        } catch (Throwable e) {
            System.out.println(22222);
        } finally {
            System.out.println(11111);
        }

        Thread.sleep(11111111);
    }

    private static void testBase() {
        ArrayList<OOMObject> list = new ArrayList<>();
        while (true) {
            list.add(new OOMObject());
            //Thread.sleep(100);
        }
    }
}
