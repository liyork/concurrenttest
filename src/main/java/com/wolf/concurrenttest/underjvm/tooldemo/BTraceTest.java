package com.wolf.concurrenttest.underjvm.tooldemo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Description: BTrace跟踪演示
 * 已在线上运行，此时想知道两个随机数是什么。
 * 暂时没有出来，jvisual那边编译错误，而直接运行btrace pid TracingScript返回0没有效果..
 * Created on 2021/7/21 10:30 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class BTraceTest {
    public int add(int a, int b) {
        return a + b;
    }

    public static void main(String[] args) throws IOException {
        BTraceTest test = new BTraceTest();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        for (int i = 0; i < 10; i++) {
            reader.readLine();
            int a = (int) Math.round(Math.random() * 1000);
            int b = (int) Math.round(Math.random() * 1000);
            System.out.println(test.add(a, b));
        }
    }
}
