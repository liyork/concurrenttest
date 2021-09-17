package com.wolf.concurrenttest.hcpta.deepthread;

import java.util.stream.IntStream;

/**
 * Description:
 * Created on 2021/9/16 1:27 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class NamedThread {
    private final static String PREFIX = "ALEX-";

    public static void main(String[] args) {
        IntStream.range(0, 5).mapToObj(NamedThread::createThread)
                .forEach(Thread::start);
    }

    private static Thread createThread(final int intName) {
        return new Thread(() -> System.out.println(Thread.currentThread().getName()),
                PREFIX + intName);
    }
}
