package com.wolf.concurrenttest.volatiletest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Description:通过javap 查看volatile原码如何布局,似乎两者一样。。。
 * <br/> Created on 2017/6/24 22:14
 *
 * @author 李超
 * @since 1.0.0
 */
public class VolatileSourceCodeTest {

    private static volatile int a = 1;
    private static  int b = 2;

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                while(a == 1) {

                }
                System.out.println("a is not 1,a=" + a);
            }
        });
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                while(b == 2) {

                }
                System.out.println("b is not 2,b=" + b);
            }
        });

        Thread.sleep(3000);

        a = 3;
        b = 4;

        executorService.shutdown();
    }
}
