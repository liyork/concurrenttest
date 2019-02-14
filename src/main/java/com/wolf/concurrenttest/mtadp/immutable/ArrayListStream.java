package com.wolf.concurrenttest.mtadp.immutable;

import java.util.Arrays;
import java.util.List;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/01
 */
public class ArrayListStream {

    public static void main(String[] args) {
        testStream();
    }

    //stream中每个操作都是全新的list，不会有并发问题
    private static void testStream() {
        List<String> list = Arrays.asList("java", "thread", "concurrency", "scala", "clojure");

        list.parallelStream().map(String::toUpperCase).forEach(System.out::println);
        list.forEach(System.out::println);
    }
}
