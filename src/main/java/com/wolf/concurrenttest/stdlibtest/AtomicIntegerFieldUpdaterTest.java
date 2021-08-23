package com.wolf.concurrenttest.stdlibtest;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * Description:
 * AtomicIntegerFieldUpdater 约束：需要可访问到字段、需要volatile、不支持static
 *
 * <br/> Created on 21/03/2018 10:15 AM
 *
 * @author 李超
 * @since 1.0.0
 */
public class AtomicIntegerFieldUpdaterTest {

    public static void main(String[] args) throws InterruptedException {
        AtomicIntegerFieldUpdater<Candidate> updater = AtomicIntegerFieldUpdater.newUpdater(Candidate.class, "score1");
        AtomicInteger check = new AtomicInteger();
        Candidate candidate = new Candidate();
        for (int i = 0; i < 100; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    updater.incrementAndGet(candidate);
                    check.incrementAndGet();
                }
            }).start();
        }

        //为什么本地测试不可以？？？candidate.score:99  check:100
        //开始还以为是变量名score问题，但是改了也不行，想想也不可能是这个原因
        //是不是直接获取candidate.score1打印有问题，那么也同时打印updater.get(candidate)，再想想也不可能，因为volatile保证了可见性。
        //o，是main线程提前获取了，没等线程执行完成。加上sleep就好了。

        Thread.sleep(1000);
        System.out.println("candidate.score:" + candidate.score1);
        System.out.println("updater.get(candidate):" + updater.get(candidate));
        System.out.println("check:" + check.get());
    }

    public static class Candidate {
        int id;
        volatile int score1;
    }
}
