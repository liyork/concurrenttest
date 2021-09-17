package com.wolf.concurrenttest.hcpta.deepthread;

import java.util.stream.IntStream;

/**
 * Description:
 * Created on 2021/9/16 9:04 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ThreadDefaultName {
    public static void main(String[] args) {
        IntStream.range(0, 5).boxed().map(i ->
                new Thread(() -> System.out.println(Thread.currentThread().getName())))
                .forEach(Thread::start);
    }
}
