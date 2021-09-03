package com.wolf.concurrenttest.bfbczm.foundation;

/**
 * Description: 非守护线程测试
 * Created on 2021/9/3 6:30 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class DaemonTest {
    public static void main(String[] args) {
        Thread thread = new Thread(() -> {
            for (; ; ) {

            }
        });

        // 设置为守护线程
        //thread.setDaemon(true);

        // 启动子线程
        thread.start();
        System.out.println("main thread is over");
    }
}
