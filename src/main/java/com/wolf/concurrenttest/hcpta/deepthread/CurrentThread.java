package com.wolf.concurrenttest.hcpta.deepthread;

/**
 * Description:
 * Created on 2021/9/17 7:26 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class CurrentThread {
    public static void main(String[] args) {
        new Thread() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread() == this);
            }
        }.start();

        String name = Thread.currentThread().getName();
        System.out.println("main".equals(name));
    }
}
