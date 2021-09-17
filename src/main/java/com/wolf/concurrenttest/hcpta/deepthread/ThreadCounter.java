package com.wolf.concurrenttest.hcpta.deepthread;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Description:
 * java -Xmx256m -Xms64m ThreadCounter
 * Created on 2021/9/16 9:40 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ThreadCounter extends Thread {
    final static AtomicInteger counter = new AtomicInteger(0);

    @Override
    public void run() {
        try {
            System.out.println("The " + counter.getAndIncrement() + " thread be created");
            TimeUnit.MILLISECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            while (true) {
                new ThreadCounter().start();
            }
        } catch (Throwable throwable) {
            System.out.println("failed At=> " + counter.get());
        }
    }
}
