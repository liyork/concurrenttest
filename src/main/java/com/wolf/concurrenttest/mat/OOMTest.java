package com.wolf.concurrenttest.mat;

import java.util.LinkedList;
import java.util.List;

/**
 * Description:
 * -Xmx32M
 * -XX:+HeapDumpOnOutOfMemoryError
 * -XX:HeapDumpPath=/Users/chaoli/Downloads/dump/OOMTest.hprof
 * Created on 2021/9/14 6:46 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class OOMTest {
    private static final List<byte[]> bsList = new LinkedList<>();

    public static void main(String[] args) {
        int size = 1024 * 1024 * 10;
        int count = 0;
        while (true) {
            bsList.add(new byte[size]);
            System.out.println("Add 10M byte[]: " + ++count);
        }
    }
}
