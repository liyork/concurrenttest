package com.wolf.concurrenttest.taojcp.tools;

import java.util.concurrent.CyclicBarrier;

/**
 * Description: CyclicBarrier测试，可重复用，都到达则一起执行
 * Created on 2021/8/31 1:06 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class CyclicBarrierTest {
    static CyclicBarrier c = new CyclicBarrier(2);

    public static void main(String[] args) {
        new Thread(() -> {
            try {
                c.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(1);
        }).start();

        try {
            c.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(2);
    }
}
