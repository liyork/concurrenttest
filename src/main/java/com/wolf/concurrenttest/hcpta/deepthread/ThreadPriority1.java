package com.wolf.concurrenttest.hcpta.deepthread;

/**
 * Description: 线程优先级不能高于所在组
 * Created on 2021/9/17 7:05 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ThreadPriority1 {
    public static void main(String[] args) {
        ThreadGroup group = new ThreadGroup("test");
        group.setMaxPriority(7);

        Thread thread = new Thread(group, "test-thread");
        thread.setPriority(10);
        System.out.println(thread.getPriority());
    }
}
