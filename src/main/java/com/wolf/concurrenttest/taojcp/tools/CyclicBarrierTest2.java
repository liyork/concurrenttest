package com.wolf.concurrenttest.taojcp.tools;

import java.util.concurrent.CyclicBarrier;

/**
 * Description: 都到达时，先执行指定A，然后各自执行
 * Created on 2021/8/31 1:06 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class CyclicBarrierTest2 {
    static CyclicBarrier c = new CyclicBarrier(2, new A());

    public static void main(String[] args) {
        new Thread(() -> {
            try {
                c.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //SleepUtils.second(2);
            System.out.println(1);
        }).start();

        try {
            c.await();
        } catch (Exception e) {
            e.printStackTrace();
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
