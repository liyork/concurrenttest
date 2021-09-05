package com.wolf.concurrenttest.bfbczm.list;

import java.util.ArrayList;

/**
 * Description: 索引测试
 * Created on 2021/9/5 9:03 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class IndexTest {
    public static void main(String[] args) {
        ArrayList<String> strings = new ArrayList<>();
        strings.add("1");
        strings.add("2");
        strings.add("3");

        // 用下标，从0开始
        String remove = strings.remove(2);
        System.out.println(remove);
        System.out.println(strings);
    }
}
