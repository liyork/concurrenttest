package com.wolf.concurrenttest.tooltest;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Description:
 * 1.8同样使用分段锁，不过不采用segment概念，而是采用原始hashmap结构+cas+synchronized+volatile，只是听说以前用的segment概念分段锁。
 * 现在是数组中包含node有next节点，大于阈值则使用红黑树
 * <br/> Created on 2017/6/23 17:04
 *
 * @author 李超
 * @since 1.0.0
 */
public class ConcurrentHashMapTest {

    public static void main(String[] args) {

//        testBase();

        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                Integer result = testConcurrentInitialMap("a", 1);
                System.out.println(Thread.currentThread().getName() + "_getResult:" + result);
            }).start();
        }
    }

    private static void testBase() {
        final ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + "_begin");
                map.put("q1", 7);
                System.out.println(Thread.currentThread().getName() + "_end");
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + "_begin");
                map.put("c", 3);
                System.out.println(Thread.currentThread().getName() + "_end");
            }
        }).start();

        ConcurrentLinkedDeque<Integer> integers = new ConcurrentLinkedDeque<>();
        integers.add(1);
        integers.add(2);
        integers.add(3);
        integers.add(4);
    }

    private static Map<String, Integer> map = new ConcurrentHashMap<>();

    //只有第一次放入的才能成功返回
    private static Integer testConcurrentInitialMap(String key, Integer value) {

        Integer result = map.get(key);
        if (null == result) {
            result = map.putIfAbsent(key, value);
            if (result == null) {
                System.out.println(Thread.currentThread().getName() + " getvalue is null");
            }
        }

        return result;
    }
}
