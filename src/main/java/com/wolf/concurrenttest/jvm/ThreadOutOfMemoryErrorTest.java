package com.wolf.concurrenttest.jvm;

import java.util.concurrent.CountDownLatch;

/**
 * Description:
 * <br/> Created on 2017/6/9 11:25
 *
 * @author 李超
 * @since 1.0.0
 */
public class ThreadOutOfMemoryErrorTest {


    public static void main(String[] args) throws InterruptedException {

        for(int i = 0; ; i++) {
            Thread.sleep(5);
            System.out.println("i = " + i);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    CountDownLatch cdl = new CountDownLatch(1);
                    try {
                        cdl.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

}

