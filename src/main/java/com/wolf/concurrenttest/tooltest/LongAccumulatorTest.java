package com.wolf.concurrenttest.tooltest;

import java.util.Random;
import java.util.concurrent.atomic.LongAccumulator;

/**
 * Description:
 * 原子性调用任意函数
 * <br/> Created on 24/03/2018 8:32 PM
 *
 * @author 李超
 * @since 1.0.0
 */
public class LongAccumulatorTest {

    public static void main(String[] args) throws InterruptedException {
        LongAccumulator accumulator = new LongAccumulator(Long::max,Long.MIN_VALUE);
        Thread[] threads = new Thread[1000];

        for (int i = 0; i < 1000; i++) {
            threads[i] = new Thread(() -> {
                Random random = new Random();
                long value = random.nextLong();
                accumulator.accumulate(value);
            });
            threads[i].start();
        }

        for (int i = 0; i < 1000; i++) {
            threads[i].join();
        }

        System.out.println(accumulator.longValue());//对所有的cell进行Long::max
    }
}
