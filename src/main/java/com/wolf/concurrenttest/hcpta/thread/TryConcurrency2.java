package com.wolf.concurrenttest.hcpta.thread;

import java.util.concurrent.TimeUnit;

/**
 * Description: lambda改造
 * Created on 2021/9/15 1:05 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class TryConcurrency2 {
    public static void main(String[] args) throws InterruptedException {
        new Thread(TryConcurrency2::enjoyMusic).start();
        browseNews();
    }

    private static void browseNews() {
        for (; ; ) {
            System.out.println("Uh-hub, the good news.");
            sleep();
        }
    }

    private static void enjoyMusic() {
        for (; ; ) {
            System.out.println("Un-hub, the nic music.");
            sleep();
        }
    }

    private static void sleep() {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
