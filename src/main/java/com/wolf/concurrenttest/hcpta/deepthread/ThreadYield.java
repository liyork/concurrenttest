package com.wolf.concurrenttest.hcpta.deepthread;

import java.util.stream.IntStream;

/**
 * Description:
 * Created on 2021/9/17 6:43 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ThreadYield {
    public static void main(String[] args) {
        IntStream.range(0, 2).mapToObj(ThreadYield::create)
                .forEach(Thread::start);
    }

    private static Thread create(int index) {
        return new Thread(() -> {
            if (index == 0) {
                Thread.yield();
            }
            System.out.println(index);
        });
    }
}
