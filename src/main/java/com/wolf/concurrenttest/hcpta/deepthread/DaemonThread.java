package com.wolf.concurrenttest.hcpta.deepthread;

/**
 * Description: 守护线程可被继承
 * Created on 2021/9/16 9:52 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class DaemonThread {
    public static void main(String[] args) throws InterruptedException {
        // main线程开始

        // 默认是非守护线程
        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        // 设定为守护线程，要在启动前设定
        thread.setDaemon(true);

        thread.start();
        Thread.sleep(2_000L);
        System.out.println("main thread finished lifecycle.");

        // main线程结束
    }
}
