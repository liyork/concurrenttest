package com.wolf.concurrenttest.hcpta.group;

import java.util.concurrent.TimeUnit;

/**
 * Description:
 * mainGroup
 * + mainThread
 * + myGroup1
 * + + myGroup2
 * Created on 2021/9/19 9:44 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ThreadGroupEnumerateThreadGroups {
    public static void main(String[] args) throws InterruptedException {
        ThreadGroup myGroup1 = new ThreadGroup("myGroup1");
        new ThreadGroup(myGroup1, "myGroup2");

        TimeUnit.MILLISECONDS.sleep(2);
        ThreadGroup mainGroup = Thread.currentThread().getThreadGroup();

        ThreadGroup[] list = new ThreadGroup[mainGroup.activeGroupCount()];

        int recurseSize = mainGroup.enumerate(list);
        System.out.println(recurseSize);

        recurseSize = mainGroup.enumerate(list, false);
        System.out.println(recurseSize);
    }
}
