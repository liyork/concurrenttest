package com.wolf.concurrenttest.hcpta.deepthread;

/**
 * Description:
 * Created on 2021/9/16 1:38 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ThreadConstruction {
    public static void main(String[] args) {
        Thread t1 = new Thread("t1");

        ThreadGroup group = new ThreadGroup("TestGroup");
        Thread t2 = new Thread(group, "t2");

        ThreadGroup mainThreadGroup = Thread.currentThread().getThreadGroup();
        System.out.println("main thread belong group: " + mainThreadGroup.getName());
        System.out.println("t1 and main belong the same group: " + (mainThreadGroup == t1.getThreadGroup()));
        System.out.println("t2 thread group not belong main group: " + (mainThreadGroup != t2.getThreadGroup()));
        System.out.println("t2 thread group not belong TestGroup: " + (group == t2.getThreadGroup()));
    }
}
