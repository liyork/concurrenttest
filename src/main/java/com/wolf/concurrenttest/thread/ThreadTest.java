package com.wolf.concurrenttest.thread;

import com.wolf.concurrenttest.common.TakeTimeUtils;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * 线程简单测试
 * <p>
 * 在JAVA环境中，线程Thread有如下几个状态：
 * <p>
 * 1.NEW，thread对象创建但是没有执行
 * 2.RUNNABLE(NEW状态调用start之后、RUNNING状态cpu调度轮训暂时切换到其他线程、RUNNING状态调用yield、BLOCKED状态读取io结束、
 * BLOCKED状态休眠结束、BLOCKED状态被notify、notifyall、BLOCKED状态获取锁资源、BLOCKED状态被interrupt，NEW/RUNNABLE状态cpu schedule)
 * 3.BLOCKED(RUNNING状态调用了wait、sleep、阻塞io、请求锁)
 * 4.WAITING，正在等待其他线程的动作
 * 5.TIMED_WAITING，正在等待其他线程的动作，带有超时
 * 6.TERMINATED：最终状态(RUNNING状态调用了stop、BLOCKED状态调用了stop或jvm crash、正常运行结束、异常结束线程)
 * 状态转换见：thread_lifecycle.png
 * 给定时间内，线程只能处于一个状态。这些状态不能映射到操作系统的线程状态，时JVM使用的状态。
 * <p>
 * sychronized保证多线程访问共享数据的同步性，即先来后到
 * <p>
 * 线程执行是很随机的，若有释放锁的，竞争cpu不一定会给哪个线程执行片段，所以不要指望相互竞争的线程有哪些顺序
 * <p>
 * Runnable出现则将线程的控制和业务逻辑彻底分离。职责单一，相互分离。runnable执行单元可重复利用。
 * <p>
 * stackSize属性大则递归深度大，小则可以创建多数线程，另见jvm_sum.md
 * <p>
 * Date: 2015/9/23
 * Time: 17:02
 *
 * @author 李超
 * @version 1.0
 * @since 1.0
 */
public class ThreadTest {

    private static Object monitor = new Object();

    public static void main(String[] args) throws InterruptedException {

//        testDaemon();
//        testSleepUseTimeUnit();
//        testYield();

//        testWaitAndSleep();

////        testThis();
////        testIsAlive();
//
////        testJoin1();
////        testJoin2();
//        testSimulateCountDownLatch();
////        testWaitShouldInSynScope();
////        testWaitTime();

////        testLocalVariable();

////        testUncaughtExceptionHandler();
////        testDefaultUncaughtExceptionHandler();
////        testAllHandleException();
//
////        testDirtyRead();
////        testExceptionReleaseLock();
//
////        testMultiSynJudge();
////        testStringPool();
//
////        testGoodSuspendResume();
////        testExceptionNotAffectMainThread();
//
////        testWaitNotify1();

//        testExit();

        testGetAllThread();
    }

    //jconsole可以调出空出台，选择thread查看当前有多少线程在执行。本例有main和thread-0，还有其他的守护线程
    //run默认调用target.run
    //类似模板模式，父类控制程序结构，调用run，子类实现run即可或者构造runnable放入被调用run方法。
    @Test
    public void testStart() {

        Runnable runnable = () -> {
            while (true) {
                System.out.println("1111");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        Thread thread = new Thread(runnable, "threadStart-thread");//仅仅创建了thread对象

        thread.start();//底层调用native方法启动线程并调用run方法。非阻塞方法

        while (true) {
            System.out.println("22222");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //启动两次则java.lang.IllegalThreadStateException，不论是否线程终止，只能调用一次
    @Test
    public void testTwoStart() {

        Runnable runnable = () -> {
            System.out.println("1111");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();
        //thread.start();//并发启动

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        thread.start();//线程结束后再启动
    }

    //默认线程名称
    @Test
    public void testDefaultThreadName() {

        IntStream.range(0, 5).boxed().map(i -> new Thread(
                () -> System.out.println(Thread.currentThread().getName()))
        ).forEach(Thread::start);
    }

    @Test
    public void testAssignThreadName() {

        String prefix = "testAssignThread-";
        IntStream.range(0, 5).boxed().map(i -> new Thread(
                () -> System.out.println(Thread.currentThread().getName()), prefix + i)
        ).forEach(Thread::start);

        Thread thread = new Thread(() -> System.out.println(Thread.currentThread().getName()));
        thread.setName("lastOpportunitySetName");
        thread.start();
    }

    /**
     * JVM会在所有的非守护线程（用户线程）执行完毕后退出
     * main线程是用户线程，但main线程执行完毕，不能决定JVM是否退出，可能还有其他用户线程在执行。
     * 守护线程的作用：处理后台工作，比如JVM的垃圾回收线程。
     * 守护线程具备自动结束生命周期的特性。
     */
    public static void testDaemon() throws InterruptedException {

        Thread thread = new Thread(
                () -> {
                    System.out.println(Thread.currentThread().getName() + ":" + Thread.currentThread().isDaemon());
                    new Thread(
                            () -> System.out.println(Thread.currentThread().getName() + ":" + Thread.currentThread().isDaemon()),
                            "subThread")
                            .start();//子线程同父线程的daemon
                },
                "first");

        thread.setDaemon(true);//必须在thread.start()之前设置
        thread.start();

        Thread thread1 = new Thread(() -> {
            try {
                for (int i = 0; i < 1000; i++) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(Thread.currentThread().getName() + ":" + Thread.currentThread().isDaemon());
                }
            } finally {
                System.out.println("finally not always run。。。");//Daemon线程不一定执行,不能依靠finally释放资源
            }
        }, "second");

        thread1.setDaemon(true);//打开后，则所有用户线程执行完直接退出，不会等到thread1执行完run方法
        thread1.start();

        Thread.sleep(2000);

        System.out.println(Thread.currentThread().getName() + ":" + Thread.currentThread().isDaemon());
    }

    //线程执行单元不允许抛出checked异常，若想结束线程,则结束循环或者throw一个unchecked异常
    //休眠3小时24分17秒88毫秒，TimeUnit更方便
    public static void testSleepUseTimeUnit() throws InterruptedException {
        Thread.sleep(12257088l);

        TimeUnit.HOURS.sleep(3);
        TimeUnit.MINUTES.sleep(24);
        TimeUnit.SECONDS.sleep(17);
        TimeUnit.MILLISECONDS.sleep(88);
    }

    //提醒(hint)调度器自己愿意放弃当前CPU资源，如果CPU资源不紧张，则会忽略这种提醒。(RUNNING->RUNNABLE)
    //至少一种暗示并不一定能响应。
    private static void testYield() {

        ExecutorService exec = Executors.newFixedThreadPool(2);

        Runnable add = () -> {
            for (int i = 0; i < 30; i++) {
                System.out.println(Thread.currentThread().getName() + "test yield " + i);
                //让出当前线程，与其他线程一起竞争CPU
                Thread.yield();
            }
        };

        Runnable add1 = () -> {
            for (int i = 0; i < 30; i++) {
                System.out.println(Thread.currentThread().getName() + "not yield " + i);
            }
        };

        exec.submit(add);
        exec.submit(add1);

        exec.shutdown();
    }

    //sleep必然导致当前线程暂停指定时间，没有CPU时间片的消耗，释放CPU资源，RUNNING->BLOCK，会捕捉到interrupt信号。
    //yield只是对CPU调度器一个提示，若成功，释放CPU资源。RUNNING->RUNNABLE，不会捕捉interrupt
    public void testSleepAndYield() {

    }

    //进程和线程都有优先级概念，都是hint。不要强依赖这种概念。
    @Test
    public void testPriority() {

        Thread t1 = new Thread();
        System.out.println("t1 priority:" + t1.getPriority());
    }

    //从1开始
    @Test
    public void testId() {

        Thread t1 = new Thread();
        System.out.println("t1 id:" + t1.getId());
        System.out.println("main id:" + Thread.currentThread().getId());
    }

    @Test
    public void testThis() throws InterruptedException {
        CountOperate target = new CountOperate();
        Thread thread = new Thread(target);
        thread.setName("A");
        thread.start();

        thread.join();
    }

    //join =================xx.join,暂停当前线程，直到xx结束唤醒当前线程，由于方法是synchronized，用的是this锁
    //join内部也是调用了wait，监视的对象就是调用xx.join的xx，当前调用线程等待
    //监视对象内部有等待队列，谁监视，若没有获取锁，则进入等待队列

    public static void testJoin1() throws InterruptedException {
        System.out.println("before testJoin...");

        final Thread subThread = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                System.out.println(i);
            }
        }, "threadname");

        subThread.start();

        subThread.join();//主线程调用thread对象的wait进入thread对象的等待队列,是个同步方法。
        // 当subThread线程执行完后(或非正常死亡时抛出InterruptedException)自动调用notifyall，
        // 所以join中判断thread是否存活，然后跳出来。
//        subThread.join(10000);带超时时间

        System.out.println("main stop ");
    }

    public static void testJoin2() {
        Thread t1 = new Thread(new ThreadJoinA(), "threadname1");
        Thread t2 = new Thread(new ThreadJoinB(t1), "threadname2");
        t1.start();
        t2.start();

        //在t2中线程中t1.join，不会影响main
        System.out.println("main ...");
    }


    public static void testSimulateCountDownLatch() throws InterruptedException {
        System.out.println("before test...");

        Thread thread = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                System.out.println("xxxx==>" + i);
            }
        }, "threadname1");

        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                System.out.println("yyyy==>" + i);
            }
        }, "threadname2");


        thread.start();
        thread2.start();

        //1和2线程交互执行，但是main处于阻塞，直到1和2都执行完
        thread.join();
        thread2.join();

        System.out.println("main stop ");
    }

    //wait方法需要在同步范围内被调用，否则IllegalMonitorStateException
    //当前线程在某个对象上等待。
    private static void testWaitShouldInSynScope() throws InterruptedException {
        monitor.wait();
    }

    //wait============
    //Causes the ```current thread``` to wait until either another thread invokes the notify() method or
    // the notifyAll() method for this object, or a specified amount of time has elapsed.
    //The current thread must own this object's monitor.
    //即当前线程等待，然后进入的是调用的那个对象的等待池
    //wait+notify机制本质上是一种基于条件队列的同步
    //wait的等待一般是有条件的，而这个条件也一般是用while，防治因为意外被唤醒但是条件却为满足而执行的错误操作。

    //锁自动释放
    public static void testWaitTime() {
        Object lock = new Object();
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (lock) {
                    try {
                        System.out.println(this);//com.wolf.concurrenttest.thread.ThreadTest$9@7c406c0
                        lock.wait(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        System.out.println("main...");
    }

    //都阻塞，可中断
    //wait要先获取锁，是object方法
    //sleep不用获取锁，是thread方法，若在锁中不会放弃锁的所有权
    public static void testWaitAndSleep() {

        ExecutorService exec = Executors.newFixedThreadPool(4);
        Object lock = new Object();

        Runnable add = () -> {
            System.out.println("Pre " + lock.toString());

            try {
                //wait释放锁
//                synchronized (lock) {
//                    lock.wait();
//                }

                //sleep不释放锁
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                System.out.println("Post " + lock.toString());
            }
        };

        for (int index = 0; index < 4; index++) {
            exec.submit(add);
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        exec.shutdown();
    }

    /**
     * 局部变量不会产生线程问题，只有修改公用的地方才会有线程问题
     */
    public static void testLocalVariable() {
        final ExecutorService exec = Executors.newFixedThreadPool(4);

        final Runnable add = new Runnable() {
            public void run() {
                test();
            }
        };

        for (int index = 0; index < 20; index++) {
            exec.submit(add);
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        exec.shutdown();
    }

    private static void test() {
        int a = 1;
        a = a + 2;
        System.out.println(a);
    }

    //准备---未执行完，都是true
    private static void testIsAlive() {
        Thread thread = new Thread(() ->
                System.out.println("run...")
        );
        System.out.println("thread.isAlive:" + thread.isAlive());
        thread.start();
        System.out.println("thread.isAlive:" + thread.isAlive());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("thread.isAlive:" + thread.isAlive());
    }


    //若读不加synchronized则可以与set并发执行，那么容易产生中间结果。synchronized可能被认为是原子的，里面的内容有相互依赖性
    private static void testDirtyRead() throws InterruptedException {

        SynMethodClass synMethodClass = new SynMethodClass();

        Thread thread = new Thread(() -> {
            System.out.println("run...");
            synMethodClass.setValue(33, 44);
        }
        );

        thread.start();
        Thread.sleep(10);

        synMethodClass.printValue();
    }

    //线程中异常未补获则停止线程并释放锁
    private static void testExceptionReleaseLock() throws InterruptedException {

        SynMethodClass synMethodClass = new SynMethodClass();

        Thread thread = new Thread(() -> {
            System.out.println("thread run...");
            try {
                synMethodClass.exceptionMethod();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        );

        Thread thread2 = new Thread(() -> {
            System.out.println("thread run...");
            synMethodClass.noExceptionMethod();
        }
        );

        thread.start();
        thread2.start();
    }

    //即使Vector的add是同步的，但是由于多个线程执行顺序是未知的，这时掺杂了其他判断条件的情况下若未整体同步，则结果不正确。
    //关键由于业务对整体有同步限制，单细粒度同步无用。
    private static void testMultiSynJudge() throws InterruptedException {

        SynMethodClass synMethodClass = new SynMethodClass();

        Thread thread = new Thread(() -> {
            System.out.println("thread1 run...");
            try {
                synMethodClass.multiJudge("xx");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        );


        Thread thread2 = new Thread(() -> {
            System.out.println("thread2 run...");
            try {
                synMethodClass.multiJudge("yy");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        );

        thread.start();
        thread2.start();

        thread.join();
        thread2.join();


        System.out.println(synMethodClass.vector.size());
    }

    //string有些被池化，同步时要小心。
    private static void testStringPool() throws InterruptedException {

        SynMethodClass synMethodClass = new SynMethodClass();

        Thread thread = new Thread(() -> {
            System.out.println("thread1 run...");
            try {
                synMethodClass.stringPool("xx");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        );


        Thread thread2 = new Thread(() -> {
            System.out.println("thread2 run...");
            try {
                synMethodClass.stringPool("xx");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        );

        thread.start();
        thread2.start();

        thread.join();
        thread2.join();
    }

    //若同步使用对象属性变化，则不影响同步效果，底层应该使用的对象特有部分作为锁标识
    //若同步块中改变了锁对象，则其他线程再进来时使用的是不同锁。这个是否有意义应用??？


    //原生suspend和resume可能产生意外现象：先resume了然后suspend，但是suspend的线程会一直占用锁线程还不退出而是挂起而且state还是runnable
    //改进后也会产生先resume后suspend，但是会释放锁，而且状态是watting
    //应用场景可能是，某个线程觉得太累了，需要休息一下然后别人再唤醒他。或者负载太高，需要自己休息暂停，把任务给别人
    private static void testGoodSuspendResume() throws InterruptedException {

        GoodSuspend target = new GoodSuspend();
        Thread thread = new Thread(target);
        thread.start();

        Thread.sleep(3000);
        target.suspend();
        Thread.sleep(5000);
        target.resume();
    }

    //实现jvm进程退出。
    private static void testExit() throws InterruptedException {

        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

//            Runtime.getRuntime().exit(0);
            System.exit(0);
        }).start();


        TimeUnit.SECONDS.sleep(10000);
    }

    private static class GoodSuspend implements Runnable {

        volatile boolean isNeedSuspend;

        public void suspend() {
            isNeedSuspend = true;
        }

        public void resume() {
            synchronized (this) {
                this.notify();
            }
            isNeedSuspend = false;
        }

        @Override
        public void run() {
            while (true) {
                System.out.println(Thread.currentThread().getName() + " is running...");

                TakeTimeUtils.simulateLongTimeOperation(800000);
                System.out.println(Thread.currentThread().getName() + " after simulateLongTimeOperation...");

                while (isNeedSuspend) {
                    System.out.println(Thread.currentThread().getName() + " run isNeedSuspend..." + System.currentTimeMillis());
                    synchronized (this) {
                        try {
                            this.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

                System.out.println(Thread.currentThread().getName() + " after while isNeedSuspend..." + System.currentTimeMillis());

            }
        }
    }


    static class ThreadJoinA implements Runnable {

        private int counter;

        @Override
        public void run() {
            while (counter <= 10) {
                System.out.print("Counter = " + counter + " ");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                counter++;
            }
            System.out.println();
        }
    }

    static class ThreadJoinB implements Runnable {

        public ThreadJoinB(Thread t1) {
            this.t1 = t1;
        }

        private Thread t1;

        @Override
        public void run() {
            System.out.println("tb ...");
            try {
                //t1只要isAlive,当前线程就会阻塞在这里
                t1.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("ThreadTesterB...");
        }
    }

    private static void testExceptionNotAffectMainThread() {
        int i = 1;

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (i == 1) {
                    throw new RuntimeException("222222");
                }
            }
        }).start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(Thread.currentThread().getName() + "_11111");
    }

    private static void testWaitNotify1() throws InterruptedException {

        Object lock = new Object();

        new Thread(new Runnable() {
            @Override
            public void run() {

                synchronized (lock) {
                    try {
                        lock.wait();//醒来第一件任务是获取锁，若未获得则一直等待。

                        System.out.println("thread 1111");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("thread 22222");
            }
        }).start();

        Thread.sleep(2000);

        System.out.println("main...before lock");
        synchronized (lock) {
            System.out.println("main...notify");
            lock.notify();

            System.out.println("main...sleep before," + System.currentTimeMillis());
            Thread.sleep(7000);
            System.out.println("main...sleep after," + System.currentTimeMillis());
        }
    }


    static class CountOperate implements Runnable {

        public CountOperate() {
            System.out.println("constructor CountOperate");
            System.out.println("Thread.currentThread().getName():" + Thread.currentThread().getName());
        }

        //运行起来后，线程调用(对象.run)执行任务
        @Override
        public void run() {
            System.out.println("run CountOperate");
            System.out.println("Thread.currentThread().getName():" + Thread.currentThread().getName());

            System.out.println("this:" + this);//当前被调用对象,因为方法属于对象的
            System.out.println("Thread.currentThread():" + Thread.currentThread());//当前执行线程
        }
    }

    private static void testGetAllThread() {

        ThreadGroup currentThreadGroup = Thread.currentThread().getThreadGroup();
        System.out.println("currentThreadGroup:" + currentThreadGroup);
        ThreadGroup abc = new ThreadGroup("abc");
        new Thread(abc, () -> {//使用abc线程组
            try {
                Thread.sleep(200000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "aaaa").start();

        new Thread(() -> {//使用当前线程(main)的线程组
            try {
                Thread.sleep(200000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "bbbb").start();

        ThreadGroup topGroup = currentThreadGroup;
        while (currentThreadGroup != null) {
            topGroup = currentThreadGroup;
            currentThreadGroup = currentThreadGroup.getParent();
        }

        System.out.println("topGroupName:" + topGroup.getName());//system
        int estimatedSize = topGroup.activeCount();
        Thread[] allThread = new Thread[estimatedSize];

        int actualSize = topGroup.enumerate(allThread);

        Thread[] list = new Thread[actualSize];
        System.arraycopy(allThread, 0, list, 0, actualSize);
        System.out.println("Thread list size == " + list.length);
        for (Thread thread : list) {
            System.out.println(thread.getName());
        }
    }
}