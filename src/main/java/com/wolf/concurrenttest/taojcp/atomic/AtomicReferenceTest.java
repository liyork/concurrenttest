package com.wolf.concurrenttest.taojcp.atomic;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Description: 原子更新引用
 * Created on 2021/8/31 12:19 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class AtomicReferenceTest {
    public static AtomicReference<User> atomicUserRef = new AtomicReference<User>();

    public static void main(String[] args) {
        User user = new User("xx", 11);
        atomicUserRef.set(user);

        User updateUser = new User("yy", 22);
        atomicUserRef.compareAndSet(user, updateUser);

        System.out.println(atomicUserRef.get().getName());
        System.out.println(atomicUserRef.get().getOld());
    }
}
