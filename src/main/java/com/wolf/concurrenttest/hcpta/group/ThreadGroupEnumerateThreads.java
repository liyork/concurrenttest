package com.wolf.concurrenttest.hcpta.group;

import java.util.concurrent.TimeUnit;

/**
 * Description:
 * mainGroup
 * + + myGroup
 * + + + myThread
 * + mainThread
 * Created on 2021/9/19 9:37 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ThreadGroupEnumerateThreads {
    public static void main(String[] args) throws InterruptedException {
        ThreadGroup myGroup = new ThreadGroup("myGroup");

        Thread thread = new Thread(myGroup, () -> {
            while (true) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "myThread");
        thread.start();

        TimeUnit.MILLISECONDS.sleep(2);
        ThreadGroup mainGroup = thread.currentThread().getThreadGroup();

        // 将mainGroup中active线程放入list，递归
        Thread[] list = new Thread[mainGroup.activeCount()];
        int recurseSize = mainGroup.enumerate(list);
        System.out.println(recurseSize);

        // 非递归放入
        recurseSize = mainGroup.enumerate(list, false);
        System.out.println(recurseSize);
    }
}
