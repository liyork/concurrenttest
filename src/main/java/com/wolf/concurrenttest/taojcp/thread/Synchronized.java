package com.wolf.concurrenttest.taojcp.thread;

/**
 * Description: 测试底层jvm针对synchronized关键字的处理方式
 * 对class文件，javap -v Synchronized
 * Created on 2021/8/27 6:41 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class Synchronized {
    public static void main(String[] args) {
        // 同步块，用Synchronized.class上锁
        synchronized (Synchronized.class) {

        }

        m();
    }

    // 静态同步方法，用Synchronized.class上锁
    public static synchronized void m() {
        System.out.println("m is running");
    }
}
