package com.wolf.concurrenttest.taojcp.atomic;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * Description: 原子更新字段
 * Created on 2021/8/31 12:24 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class AtomicIntegerFieldUpdaterTest {
    // 创建原子字段更新器，设置需要更新的对象类和对象的属性
    private static AtomicIntegerFieldUpdater<User> a = AtomicIntegerFieldUpdater.newUpdater(User.class, "old");

    public static void main(String[] args) {
        User user = new User("aaa", 10);
        System.out.println(a.getAndIncrement(user));
        System.out.println(a.get(user));
    }

}
