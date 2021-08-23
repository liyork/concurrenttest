package com.wolf.concurrenttest.stdlibtest;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;

/**
 * Description:
 * LongAdder内部使用分离热点概念，提升高并发对数据竞争产生的性能问题，性能比AtomicInteger好，但是没有返回值，不能解决最大边界值问题
 * 目前来看所有解决性能瓶颈的方向基本上都是朝着水平扩展的方向去，单点不行就多点。
 * 但是实际测试并没有快。。
 * <br/> Created on 24/03/2018 8:32 PM
 *
 * @author 李超
 * @since 1.0.0
 */
public class LongAdderTest {

    public static void main(String[] args) {
        LongAdder longAdder = new LongAdder();
        AtomicInteger atomicInteger = new AtomicInteger();

        long start = System.currentTimeMillis();
        for (int i = 0; i < 100000000; i++) {
            longAdder.increment();
        }
        System.out.println("longAdder-->" + (System.currentTimeMillis() - start) + " " + longAdder.intValue());

        long start2 = System.currentTimeMillis();
        for (int i = 0; i < 100000000; i++) {
            atomicInteger.incrementAndGet();
        }
        System.out.println("atomicInteger-->" + (System.currentTimeMillis() - start2) + " " + atomicInteger.intValue());
    }
}
