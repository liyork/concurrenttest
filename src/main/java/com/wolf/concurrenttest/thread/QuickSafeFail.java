package com.wolf.concurrenttest.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Description:
 * <br/> Created on 2017/7/5 16:05
 *
 * @author 李超
 * @since 1.0.0
 */
public class QuickSafeFail {

    public static void main(String[] args) {
//        testQuickFail();
        testSafeFail();
    }

    //基于原数组迭代，会有ConcurrentModificationException
    private static void testQuickFail() {
        final List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);

        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                list.add(5);
            }
        });

        for (Integer integer : list) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(integer);
        }
    }

    //基于拷贝原数组方式迭代
    //CopyOnWriteArrayList表达的一些思想：
    //1、读写分离，读和写分开
    //2、最终一致性
    //3、使用另外开辟空间的思路，来解决并发冲突
    private static void testSafeFail() {
        final List<Integer> list = new CopyOnWriteArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);

        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                list.add(5);
            }
        });

        for (Integer integer : list) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(integer);
        }
    }
}
