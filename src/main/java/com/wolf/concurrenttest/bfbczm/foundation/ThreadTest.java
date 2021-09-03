package com.wolf.concurrenttest.bfbczm.foundation;

/**
 * Description: 线程测试
 * Created on 2021/9/1 10:28 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ThreadTest {
    // 创建线程方式--继承Thread
    static class MyThread extends Thread {
        @Override
        public void run() {
            System.out.println("I am a child thread");
        }
    }

    public static void main(String[] args) {
        MyThread thread = new MyThread();
        // 启动线程
        thread.start();
    }
}
