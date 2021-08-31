package com.wolf.concurrenttest.taojcp.atomic;

import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * Description: 原子数组内元素更新测试
 * Created on 2021/8/31 12:15 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class AtomicIntegerArrayTest {
    static int[] value = new int[]{1, 2};

    // 当数组复制一份
    static AtomicIntegerArray ai = new AtomicIntegerArray(value);

    public static void main(String[] args) {
        ai.getAndSet(0, 3);
        System.out.println(ai.get(0));
        System.out.println(value[0]);
    }
}
