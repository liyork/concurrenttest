package com.wolf.concurrenttest.hcpta.latch;

import java.util.concurrent.TimeUnit;

/**
 * Description:
 * Created on 2021/9/25 4:12 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class LatchTest {

    public static void main(String[] args) throws InterruptedException {
        //testAlwaysWait();

        CountDownLatch latch = new CountDownLatch(4);
        new ProgrammerTravel(latch, "alax1", "bus").start();
        new ProgrammerTravel(latch, "alax2", "walking").start();
        new ProgrammerTravel(latch, "alax3", "subway").start();
        new ProgrammerTravel(latch, "alax4", "bicycle").start();

        try {
            latch.await(TimeUnit.SECONDS, 5);
            System.out.println("== all of programmer arrived ==");
        } catch (WaitTimeoutException e) {
            e.printStackTrace();
        }
    }

    private static void testAlwaysWait() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(4);
        new ProgrammerTravel(latch, "alax1", "bus").start();
        new ProgrammerTravel(latch, "alax2", "walking").start();
        new ProgrammerTravel(latch, "alax3", "subway").start();
        new ProgrammerTravel(latch, "alax4", "bicycle").start();

        latch.await();

        System.out.println("== all of programmer arrived ==");
    }
}
