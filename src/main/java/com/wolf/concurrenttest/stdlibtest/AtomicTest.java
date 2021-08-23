package com.wolf.concurrenttest.stdlibtest;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Description:可以保证数组中的内容对所有线程可见，而非仅仅是数组的引用
 * <br/> Created on 2017/6/29 15:50
 *
 * @author 李超
 * @since 1.0.0
 */
public class AtomicTest {

    public static void main(String[] args) {

//        testAtomicInteger();
//        testAtomicIntegerArray();
//        testAtomicReference();

//        testAtomicIntegerFieldUpdater();
        testBound();
    }

    private static void testAtomicIntegerFieldUpdater() {
        User user = new User("qq", 1);
        AtomicIntegerFieldUpdater atomicIntegerFieldUpdater = AtomicIntegerFieldUpdater.newUpdater(User.class, "field");
        atomicIntegerFieldUpdater.getAndAdd(user, 1);
        System.out.println(" new field:" + user.field);
    }

    //原子更新引用(多个值)
    private static void testAtomicReference() {
        User user = new User("xx", 1);
        AtomicReference<User> atomicReference = new AtomicReference<>();
        atomicReference.set(user);
        User user2 = new User("xx2", 2);
        atomicReference.compareAndSet(user, user2);
        User newUser = atomicReference.get();
        System.out.println("new name:" + newUser.name + " new age:" + newUser.age);
    }

    private static void testAtomicIntegerArray() {
        int[] array = {1, 3};
        AtomicIntegerArray atomicIntegerArray = new AtomicIntegerArray(array);
        atomicIntegerArray.incrementAndGet(0);
        System.out.println("atomicIntegerArray.get(0):" + atomicIntegerArray.get(0));
        System.out.println("array[0]:" + array[0]);
    }

    private static void testAtomicInteger() {
        AtomicInteger atomicInteger = new AtomicInteger(1);
        int original = atomicInteger.getAndIncrement();
        System.out.println("original:" + original);
        System.out.println("next:" + atomicInteger.get());
    }

    private static void testBound() {
        AtomicTest atomicInteger = new AtomicTest();
        for (int i = 0; i < 100; i++) {
            int count = atomicInteger.incrementLimitMax();
            System.out.println("count==>" + count);
        }
    }

    int maxCount = 10;
    AtomicInteger curCount = new AtomicInteger(0);

    private final int incrementLimitMax() {
        for (; ; ) {
            int current = curCount.get();
            if (current >= maxCount) {
                //如果当前值已经到了最大值，则直接返回
                return current;
            }
            int next = current + 1;
            if (curCount.compareAndSet(current, next)) {
                return next;
            }
        }
    }

    private static class User {
        private String name;
        private int age;
        public volatile int field;//字段需要volatile+public

        public User(String name, int age) {
            this.name = name;
            this.age = age;
        }
    }
}
