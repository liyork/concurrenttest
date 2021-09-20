package com.wolf.concurrenttest.hcpta.group;

import java.util.concurrent.TimeUnit;

/**
 * Description:
 * Created on 2021/9/19 10:54 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ThreadGroupBasic {
    public static void main(String[] args) throws InterruptedException {

        ThreadGroup group = new ThreadGroup("group1");
        Thread thread = new Thread(group, () -> {
            while (true) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "thread");
        thread.setDaemon(true);
        thread.start();

        // make sure the thread is started
        TimeUnit.MILLISECONDS.sleep(1);

        ThreadGroup mainGroup = Thread.currentThread().getThreadGroup();
        System.out.println("activeCount= " + mainGroup.activeCount());
        System.out.println("activeGroupCount= " + mainGroup.activeGroupCount());
        System.out.println("mainGroup getMaxPriority= " + mainGroup.getMaxPriority());

        // 设定priority前后
        System.out.println("group getMaxPriority= " + group.getMaxPriority());
        System.out.println("thread.getPriority()= " + thread.getPriority());
        // 改变组的MaxPriority，对以前的线程不影响，以后的会限制最大
        group.setMaxPriority(3);
        System.out.println("group getMaxPriority= " + group.getMaxPriority());
        System.out.println("thread.getPriority()= " + thread.getPriority());

        System.out.println("getName= " + mainGroup.getName());
        System.out.println("getParent= " + mainGroup.getParent());

        System.out.println("-----------------------------");
        mainGroup.list();
        System.out.println("-----------------------------");

        // 判断当前group是不是参数的父亲
        System.out.println("parentOf= " + mainGroup.parentOf(group));
        // 传递本身true
        System.out.println("parentOf= " + mainGroup.parentOf(mainGroup));

        // main->system->null
        System.out.println(mainGroup.getParent().getParent());
    }
}
