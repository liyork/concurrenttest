package com.wolf.concurrenttest.tooltest;

import org.junit.Test;

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

    //看其他源码还以为ConcurrentHashMap不需要bean实现hashcode和equals了呢。
    //用的object的hashcode和equals
    @Test
    public void testEquals() {
        ConcurrentHashMap<TestBean,Integer> concurrentHashMap = new ConcurrentHashMap<>();

        TestBean a1 = new TestBean(1, "a");
        concurrentHashMap.put(a1, 1);
        concurrentHashMap.put(new TestBean(1, "a"), 1);
        concurrentHashMap.put(new TestBean(1, "a"), 1);

        System.out.println(concurrentHashMap.size());
        Integer a = concurrentHashMap.get(new TestBean(1, "a"));
        System.out.println(a);
        Integer a11 = concurrentHashMap.get(a1);
        System.out.println(a11);
    }

    class TestBean {
        private int id;
        private String name;

        public TestBean(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
