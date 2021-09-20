package com.wolf.concurrenttest.hcpta.hook;

import java.util.concurrent.TimeUnit;

/**
 * Description:
 * 查找顺序：
 * 当前线程是否有handler，线程所在组不断向上，直到null时，若有default则用，否则输出到System.err中
 * Created on 2021/9/19 11:44 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class EmptyExceptionHandler {
    public static void main(String[] args) {
        ThreadGroup mainGroup = Thread.currentThread().getThreadGroup();
        System.out.println(mainGroup.getName());
        System.out.println(mainGroup.getParent());  // system
        System.out.println(mainGroup.getParent().getParent());  // null

        final Thread thread = new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(1 / 0);
        }, "test-Thread");
        thread.start();
    }
}
