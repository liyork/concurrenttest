package com.wolf.concurrenttest.bfbczm.list;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Description: 弱一致性
 * Created on 2021/9/5 9:17 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class CopyList {
    private static volatile CopyOnWriteArrayList<String> arrayList =
            new CopyOnWriteArrayList<>();

    public static void main(String[] args) throws InterruptedException {
        arrayList.add("hello1");
        arrayList.add("hello2");
        arrayList.add("hello3");
        arrayList.add("hello4");
        arrayList.add("hello5");

        Thread threadOne = new Thread(() -> {
            arrayList.set(1, "xx1");
            arrayList.remove(2);
            arrayList.remove(3);
        });

        // 保存list内array的引用
        Iterator<String> itr = arrayList.iterator();

        threadOne.start();
        threadOne.join();

        while (itr.hasNext()) {
            System.out.println(itr.next());
        }

    }
}
