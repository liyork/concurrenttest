package com.wolf.concurrenttest.lock;

import com.wolf.concurrenttest.thread.SynMethodClass;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * Description:
 * <p>
 * 栈信息：jstack 23745
 * <p>
 * "testSynchronized-4" #17 prio=5 os_prio=31 tid=0x00007fc29b005000 nid=0x5903 waiting on condition [0x00007000086f3000]
 * java.lang.Thread.State: TIMED_WAITING (sleeping)————正在休眠
 * at java.lang.Thread.sleep(Native Method)
 * at java.lang.Thread.sleep(Thread.java:340)
 * at java.util.concurrent.TimeUnit.sleep(TimeUnit.java:386)
 * at com.wolf.concurrenttest.lock.SynchronizedTest.lambda$main$0(SynchronizedTest.java:23)
 * - locked <0x000000076af43668> (a java.lang.Object)  ————持有锁0x000000076af43668
 * at com.wolf.concurrenttest.lock.SynchronizedTest$$Lambda$1/3447021.run(Unknown Source)
 * at java.lang.Thread.run(Thread.java:748)
 * <p>
 * "testSynchronized-3" #16 prio=5 os_prio=31 tid=0x00007fc29a84d800 nid=0x5803 waiting for monitor entry [0x00007000085f0000]
 * java.lang.Thread.State: BLOCKED (on object monitor)————阻塞状态
 * at com.wolf.concurrenttest.lock.SynchronizedTest.lambda$main$0(SynchronizedTest.java:23)
 * - waiting to lock <0x000000076af43668> (a java.lang.Object)————等待获取锁0x000000076af43668
 * at com.wolf.concurrenttest.lock.SynchronizedTest$$Lambda$1/3447021.run(Unknown Source)
 * at java.lang.Thread.run(Thread.java:748)
 * <p>
 * <p>
 * javap -v SynchronizedTest，查看反编译的命令
 * astore_<n>存储引用至本地变量表
 * aload_<n>从本地变量表加载引用
 * getstatic从class中获得静态属性
 * <p>
 * monitorenter
 * 每个对象都与一个monitor关联，一个monitor的lock的锁只能被一个线程同一时间获得
 * 若monitor的计数为0，则该monitor的lock还没有被获得，线程获得后+1，重入则++，其他线程则阻塞直到为0
 * <p>
 * monitorexit
 * 放弃monitor所有权
 * <p>
 * synchronized尽可能只作用于共享资源的读写作用域，减少范围。
 *
 *
 * <p>
 * <br/> Created on 2019-01-27
 *
 * @author 李超
 * @since 1.0.0
 */
public class SynchronizedTest {

    public static void main(String[] args) {

        //testLockSource();


        testSynMethod();
//        testWaitShouldInSynScope();
    }

    private static void testLockSource() {
        Object lock = new Object();

        Runnable runnable = () -> {

            lockAccess(lock);
        };

        IntStream.range(0, 5).forEach(
                i -> new Thread(runnable, "testSynchronized-" + i).start()
        );
    }

    private static void lockAccess(Object lock) {

        System.out.println("11111");

        synchronized (lock) {
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("cur thread:" + Thread.currentThread().getName());
        }
    }

    /**
     * 测试两个线程访问一个对象的两个同步方法，成员方法锁定的是当前对象，静态方法锁定的是类class，
     * 只要方法有synchronized则要先获取这个对象的锁才能访问，不能并行。不区分方法修饰符
     */
    private static void testSynMethod() {

        final SynMethodClass twoSynMethodClass = new SynMethodClass();

        new Thread(new Runnable() {
            @Override
            public void run() {
                twoSynMethodClass.test3();
            }
        }, "threadname1").start();

        //需要等待threadname1执行完
        new Thread(new Runnable() {
            @Override
            public void run() {
                twoSynMethodClass.test2();
            }
        }, "threadname2").start();

        //锁住的是class对象，直接执行
        new Thread(new Runnable() {
            @Override
            public void run() {
                SynMethodClass.test4();
            }
        }, "threadname3").start();
    }

}
