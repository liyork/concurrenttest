package com.wolf.concurrenttest.stdlibtest.cas;

import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * Description: 提供保证数组中元素原子操作的方法
 * Created on 2021/7/11 7:52 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class AtomicIntegerArrayTest {
    public static void main(String[] args) {
        AtomicIntegerArray array = new AtomicIntegerArray(1);
        boolean b = array.compareAndSet(0, 0, 1);
        System.out.println(b);
        int andAdd = array.getAndAdd(0, 1);
        System.out.println(andAdd);
        System.out.println(array.get(0));
    }
}
