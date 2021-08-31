package com.wolf.concurrenttest.taojcp.tools;

import java.util.concurrent.Exchanger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Description: 一对一交换
 * Created on 2021/8/31 1:35 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ExchangerTest {
    private static final Exchanger<String> exgr = new Exchanger<>();

    private static ExecutorService threadPool = Executors.newFixedThreadPool(2);

    public static void main(String[] args) {
        threadPool.execute(() -> {
            String A = "流水A";
            try {
                exgr.exchange(A);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        threadPool.execute(() -> {
            try {
                String B = "流水B";
                String A = exgr.exchange(B);
                System.out.println("a和b数据是否一致：" + A.equals(B) + ", A录入:" + A + ", B录入:" + B);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        threadPool.shutdown();
    }
}
