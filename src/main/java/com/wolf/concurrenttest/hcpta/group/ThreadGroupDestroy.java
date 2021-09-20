package com.wolf.concurrenttest.hcpta.group;

/**
 * Description:
 * Created on 2021/9/19 11:14 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ThreadGroupDestroy {

    public static void main(String[] args) {
        ThreadGroup group = new ThreadGroup("testGroup");
        System.out.println("group.isDestroyed= " + group.isDestroyed());

        ThreadGroup mainGroup = Thread.currentThread().getThreadGroup();
        mainGroup.list();

        // 设定标示，并将自己从其父中移除
        group.destroy();
        System.out.println("group.isDestroyed= " + group.isDestroyed());
        mainGroup.list();
    }
}
