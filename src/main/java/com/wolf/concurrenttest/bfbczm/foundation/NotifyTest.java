package com.wolf.concurrenttest.bfbczm.foundation;

import java.util.concurrent.TimeUnit;

/**
 * Description: 通知wait测试
 * Created on 2021/9/2 1:07 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class NotifyTest {
    private static volatile Object resourceA = new Object();

    public static void main(String[] args) throws InterruptedException {
        Thread threadA = new Thread(() -> {
            synchronized (resourceA) {  // 获取resourceA共享资源的监视器锁
                System.out.println("threadA get resourceA lock");
                try {
                    System.out.println("threadA begin wait");
                    resourceA.wait();  // 被放到resourceA的阻塞集合里
                    System.out.println("threadA end wait");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        Thread threadB = new Thread(() -> {
            synchronized (resourceA) {
                System.out.println("threadB get resourceA lock");
                try {
                    System.out.println("threadB begin wait");
                    resourceA.wait();
                    System.out.println("threadB end wait");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        Thread threadC = new Thread(() -> {
            synchronized (resourceA) {
                System.out.println("threadC begin notify");
                //resourceA.notify();
                resourceA.notifyAll();
            }
        });

        threadA.start();
        threadB.start();

        TimeUnit.SECONDS.sleep(1);
        threadC.start();

        threadA.join();
        threadB.join();
        threadC.join();

        System.out.println("main over");
    }
}
