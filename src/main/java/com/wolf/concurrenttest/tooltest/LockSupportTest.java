package com.wolf.concurrenttest.tooltest;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * Description:
 * <br/> Created on 18/03/2018 8:05 PM
 *
 * @author 李超
 * @since 1.0.0
 */
public class LockSupportTest {

    public static void main(String[] args) throws InterruptedException {

//        testBase();
//        testParkTime();
//        testUnparkBeforePark();
        testNotifyBeforeWait();
//        testInterrupt();
    }

    /**
     * "Thread-0" #13 prio=5 os_prio=31 tid=0x00007f9d0504c800 nid=0x5d03 waiting on condition [0x000070000374a000]
     * java.lang.Thread.State: WAITING (parking)
     * at sun.misc.Unsafe.park(Native Method)
     * - parking to wait for  <0x000000076af0dcf8> (a com.wolf.concurrenttest.tooluseage.LockSupportTest$1)
     * at java.util.concurrent.locks.LockSupport.park(LockSupport.java:175)
     * at com.wolf.concurrenttest.tooluseage.LockSupportTest$1.run(LockSupportTest.java:26)
     * at java.lang.Thread.run(Thread.java:748)
     *
     * @throws InterruptedException
     */
    private static void testBase() throws InterruptedException {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + " is running...");
//                LockSupport.park();
                LockSupport.park(this);//便于分析日志
            }
        });
        thread.start();

        System.out.println(Thread.currentThread().getName() + " is running...");
        Thread.sleep(40000);
        LockSupport.unpark(thread);
    }

    private static void testParkTime() {
        System.out.println("in testParkTime," + System.currentTimeMillis());
        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(5));
        System.out.println("aware testParkTime," + System.currentTimeMillis());
    }

    //UNSAFE.unpark基于信号量控制，即使先执行unpark，那么当执行park时由于了信号量被设置了，所以不会阻塞
    private static void testUnparkBeforePark() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + " is running...");
                LockSupport.park();
            }
        });
        thread.start();

        LockSupport.unpark(thread);
        System.out.println(Thread.currentThread().getName() + " is running...");
    }

    //与testUnparkBeforePark相反。。先notifyall之后再wait就感知不到了。。会一直等待。
    private static void testNotifyBeforeWait() {
        Object lock = new Object();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + " is running...");
                synchronized (lock) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();

        synchronized (lock) {
            lock.notifyAll();
        }
        System.out.println(Thread.currentThread().getName() + " is running...");
    }

    //park后被interrupt不报错，只是线程interrupted=true
    //park后被unpark也可以唤醒
    private static void testInterrupt() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    System.out.println(Thread.currentThread().getName() + " is running...");
                    LockSupport.park();
//                    if (Thread.currentThread().isInterrupted()) {//上面LockSupport.park对于有interrupt标记的线程不会再等待，所以要用下面清除标记
                    if (Thread.interrupted()) {
                        System.out.println("LockSupport is interrupted..");
                    } else {
                        System.out.println("LockSupport is unpark..");
//                        break;
                    }
                }
            }
        });
        thread.start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        thread.interrupt();
        System.out.println(Thread.currentThread().getName() + " execute thread.interrupt...");

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LockSupport.unpark(thread);
        System.out.println(Thread.currentThread().getName() + " execute LockSupport.unpark...");

        //测试重复unpark和park：可以。
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LockSupport.unpark(thread);
        System.out.println(Thread.currentThread().getName() + " execute LockSupport.unpark22...");
    }

}
