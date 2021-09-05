package com.wolf.concurrenttest.bfbczm.list;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Description: 迭代器测试
 * Created on 2021/9/5 9:09 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class IteratorTest {
    public static void main(String[] args) {
        CopyOnWriteArrayList<String> strings = new CopyOnWriteArrayList<>();
        strings.add("hello1");
        strings.add("hello2");

        Iterator<String> itr = strings.iterator();
        while (itr.hasNext()) {
            System.out.println(itr.next());
        }
    }
}
