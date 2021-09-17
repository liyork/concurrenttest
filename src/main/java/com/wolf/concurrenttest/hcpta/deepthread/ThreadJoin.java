package com.wolf.concurrenttest.hcpta.deepthread;

import com.wolf.concurrenttest.taojcp.thread.SleepUtils;

import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

/**
 * Description:
 * Created on 2021/9/17 12:55 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ThreadJoin {
    public static void main(String[] args) throws InterruptedException {
        List<Thread> threads = IntStream.range(1, 3)
                .mapToObj(ThreadJoin::create).collect(toList());

        threads.forEach(Thread::start);

        for (Thread thread : threads) {
            // 被main线程调用
            thread.join();
        }

        for (int i = 0; i < 10; i++) {
            System.out.println(Thread.currentThread().getName() + "#" + i);
            SleepUtils.second(1);
        }
    }

    private static Thread create(int seq) {
        return new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                System.out.println(Thread.currentThread().getName() + "#" + i);
                //SleepUtils.second(10);
            }
        }, String.valueOf(seq));
    }
}
