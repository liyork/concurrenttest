package com.wolf.concurrenttest.bfbczm.foundation;

/**
 * Description: tl测试
 * Created on 2021/9/3 7:16 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ThreadLocalTest {

    static ThreadLocal<String> localVariable = new ThreadLocal<>();

    static void print(String str) {
        // 打印当前线程本地内存中localVariable变量的值
        System.out.println(str + ":" + localVariable.get());
        //localVariable.remove();
    }

    public static void main(String[] args) {
        Thread threadOne = new Thread(() -> {
            // 设定线程threadOne中本地变量localVariable的值
            localVariable.set("threadOne local variable");
            print("threadOne");
            System.out.println("threadOne remove after:" + localVariable.get());
        });

        Thread threadTwo = new Thread(() -> {
            localVariable.set("threadTwo local variable");
            print("threadTwo");
            System.out.println("threadTwo remove after:" + localVariable.get());
        });

        threadOne.start();
        threadTwo.start();
    }
}
