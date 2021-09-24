package com.wolf.concurrenttest.hcpta.immutable;

import java.util.Arrays;
import java.util.List;

/**
 * Description: 展示不可变对象
 * 尽管list不安全，但在stream的每个操作都是一个全新的List，不会影响原始list
 * Created on 2021/9/24 6:25 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ArrayListStream {
    public static void main(String[] args) {
        List<String> list = Arrays.asList("java", "thread", "concurrency", "scala", "clojure");

        // 获取并行的stream，通过map对list中的数据加工
        list.parallelStream().map(String::toUpperCase).forEach(System.out::println);
        System.out.println();
        list.forEach(System.out::println);
    }
}
