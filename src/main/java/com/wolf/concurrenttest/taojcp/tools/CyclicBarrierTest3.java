package com.wolf.concurrenttest.taojcp.tools;

import java.util.concurrent.CyclicBarrier;

/**
 * Description: 对用CyclicBarrier的线程进行interrupt，则可用isBroken测试
 * Created on 2021/8/31 1:06 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class CyclicBarrierTest3 {
    static CyclicBarrier c = new CyclicBarrier(2);

    public static void main(String[] args) {
        Thread thread = new Thread(() -> {
            try {
                c.await();
            } catch (Exception e) {
                // 了解阻塞的线程是否被中断
                System.out.println("xxxxx==>" + c.isBroken());
            }
            //SleepUtils.second(2);
            System.out.println(1);
        });
        thread.start();
        thread.interrupt();

        try {
            c.await();
        } catch (Exception e) {
            System.out.println("yyyyy==>" + c.isBroken());
        }
        System.out.println(2);
    }

    static class A implements Runnable {

        @Override
        public void run() {
            System.out.println(3);
        }
    }
}
