package com.wolf.concurrenttest.hcpta.volatiletest;

import java.util.concurrent.TimeUnit;

/**
 * Description:
 * Created on 2021/9/22 9:11 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class VolatileFoo {
    final static int MAX = 5;
    // 修改，查看变化
    static int init_value = 0;
    //static volatile int init_value = 0;

    public static void main(String[] args) {
        // 每次判断localValue和init_value是否一致，不一致则打印内容
        new Thread(() -> {
            int localValue = init_value;
            while (localValue < MAX) {
                if (init_value != localValue) {
                    System.out.printf("The init_value is updated to [%d]\n", init_value);
                    localValue = init_value;
                }
            }
        }, "Reader").start();

        // 不断++value
        new Thread(() -> {
            int localValue = init_value;
            while (localValue < MAX) {
                System.out.printf("the init_value will be changed to [%d]\n", ++localValue);
                init_value = localValue;
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("reader thread finish");
        }, "Update").start();
    }
}
