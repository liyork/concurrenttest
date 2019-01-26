package com.wolf.concurrenttest.queue;

import java.util.concurrent.LinkedBlockingDeque;

/**
 * Description:双端队列，工作窃取
 * <br/> Created on 2018/3/13 20:16
 *
 * @author 李超
 * @since 1.0.0
 */
public class LinkedBlockingDequeTest {

    public static void main(String[] args) {
        LinkedBlockingDeque<Integer> linkedBlockingDequeSlow = new LinkedBlockingDeque<Integer>();
        LinkedBlockingDeque<Integer> linkedBlockingDequeFast = new LinkedBlockingDeque<Integer>();

        for (int i = 0; i < 10; i++) {
            linkedBlockingDequeSlow.add(i);
            linkedBlockingDequeFast.add(i + 10);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!linkedBlockingDequeSlow.isEmpty()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Integer last = linkedBlockingDequeSlow.pollLast();
                    if (null != last) {//可能isEmpty为false，但是这时有人pollFirst，那么再pollLast实就是空。尽管单个操作都是上锁，但是isEmpty和pollLast两个操作没有上锁不是原子
                        System.out.println(Thread.currentThread().getName() + " pollLast:"+last);
                    }
                }
            }
        }, "slowthread").start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!linkedBlockingDequeFast.isEmpty()) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Integer last = linkedBlockingDequeFast.pollLast();
                    System.out.println(Thread.currentThread().getName() + " pollLast:"+last);
                }
                while (!linkedBlockingDequeSlow.isEmpty()) {
                    System.out.println("linkedBlockingDequeSlow.isEmpty():"+linkedBlockingDequeSlow.isEmpty());
                    Integer first = linkedBlockingDequeSlow.pollFirst();
                    System.out.println(Thread.currentThread().getName() + " pollFirst:"+first);
                }
            }
        }, "fastthread").start();

    }
}
