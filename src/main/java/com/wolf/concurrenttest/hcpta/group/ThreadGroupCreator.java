package com.wolf.concurrenttest.hcpta.group;

/**
 * Description:
 * Created on 2021/9/19 9:22 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ThreadGroupCreator {
    public static void main(String[] args) {
        ThreadGroup currentGroup = Thread.currentThread().getThreadGroup();
        // 当前线程所在组，作为其父ThreadGroup
        ThreadGroup group1 = new ThreadGroup("group1");

        System.out.println(group1.getParent() == currentGroup);

        // 指定父ThreadGroup
        ThreadGroup group2 = new ThreadGroup(group1, "group2");
        System.out.println(group2.getParent() == group1);
    }
}
