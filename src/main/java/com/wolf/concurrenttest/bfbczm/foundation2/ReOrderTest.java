package com.wolf.concurrenttest.bfbczm.foundation2;

import java.util.concurrent.TimeUnit;

/**
 * Description: 重排序下对多线程的影响
 * 3、4没有依赖，若执行4、1、2、3，那么ReadThread看不到num值
 * 重排序在多线程下会导致非预期的程序执行结果，而用volatile即可避免重排序和内存可见性问题
 * 写volatile变量时，可以确保volatile写之前的操作不会被编译器重排序到volatile写之后
 * 读volatile变量时，可以确保volatile读之后的操作不会被编译器重排序到volatile读之前
 * Created on 2021/9/4 6:51 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ReOrderTest {
    private static boolean ready = false;  // 改成volatile即可
    private static int num = 0;

    static class ReadThread extends Thread {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                if (ready) {  // 1
                    System.out.println(num + num);  // 2
                }
                System.out.println("read thread...");
            }
        }
    }

    static class WriteThread extends Thread {
        @Override
        public void run() {
            num = 2;  // 3
            ready = true;  // 4
            System.out.println("write thread set over...");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ReadThread rt = new ReadThread();
        rt.start();
        new WriteThread().start();

        TimeUnit.SECONDS.sleep(5);
        rt.interrupt();
        System.out.println("main exit");
    }
}
