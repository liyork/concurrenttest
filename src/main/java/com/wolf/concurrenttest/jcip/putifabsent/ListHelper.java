package com.wolf.concurrenttest.jcip.putifabsent;

import net.jcip.annotations.ThreadSafe;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Description: Implementing Put-if-absent with Client-side Locking
 * client-side locking is event more fragile because it entails putting locking code for class C into classes that are totally unrelated to C
 * just as extension violates encapsulation of implementation, client-side locking violates encapsulation of synchronization policy
 * Created on 2021/6/28 10:09 PM
 *
 * @author 李超
 * @version 0.0.1
 */
@ThreadSafe
public class ListHelper<E> {
    public List<E> list = Collections.synchronizedList(new ArrayList<E>());

    public ListHelper() {
        System.out.println(this);  // 当前实例，ListHelper的实例
    }

    public synchronized boolean putIfAbsent1(E x) {// 用的是ListHelper实例对象的锁
        System.out.println(this);
        boolean absent = !list.contains(x);
        if (absent) {
            list.add(x);
        }
        return absent;
    }

    public boolean putIfAbsent(E x) {
        System.out.println(this);  // this表示调用者，与构造器中的this一致
        synchronized (list) {// 用的是SynchronizedRandomAccessList实例对象的锁，与list是一个，也就是SynchronizedRandomAccessList内的相关方法用的mutex锁
            boolean absent = !list.contains(x);
            if (absent) {
                list.add(x);
            }
            return absent;
        }
    }

    public static void main(String[] args) throws Exception {
        ListHelper listHelper = new ListHelper();
        System.out.println(listHelper);

        listHelper.putIfAbsent("abc");
        Field mutex = listHelper.list.getClass().getSuperclass().getSuperclass().getDeclaredField("mutex");
        mutex.setAccessible(true);
        Object o = mutex.get(listHelper.list);
        System.out.println(o == listHelper.list);

        listHelper.list.get(0);
    }
}
