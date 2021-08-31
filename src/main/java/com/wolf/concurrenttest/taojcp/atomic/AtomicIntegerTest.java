package com.wolf.concurrenttest.taojcp.atomic;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Description: 原子基础类更新测试
 * Created on 2021/8/31 12:09 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class AtomicIntegerTest {
    static AtomicInteger ai = new AtomicInteger(1);

    public static void main(String[] args) {
        System.out.println(ai.getAndIncrement());
        System.out.println(ai.get());
    }
}
