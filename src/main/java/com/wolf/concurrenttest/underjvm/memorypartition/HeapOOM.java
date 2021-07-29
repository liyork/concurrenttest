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
        ArrayList<OOMObject> list = new ArrayList<>();
        while (true) {
            list.add(new OOMObject());
            //Thread.sleep(100);
        }
    }
}
