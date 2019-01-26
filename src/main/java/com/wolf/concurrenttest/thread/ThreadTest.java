package com.wolf.concurrenttest.thread;

import com.wolf.concurrenttest.common.TakeTimeUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 线程简单测试
 * <p>
 * 在JAVA环境中，线程Thread有如下几个状态：
 * <p>
 * 1，新建状态
 * 2，就绪状态
 * 3，运行状态
 * 4，阻塞状态
 * 5，死亡状态
 * <p>
 * sychronized保证多线程访问共享数据的同步性，即先来后到
 * <p>
 * 线程执行是很随机的，若有释放锁的，竞争cpu不一定会给哪个线程执行片段，所以不要指望相互竞争的线程有哪些顺序
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
//        testStart();
//        testThis();
//        testIsAlive();

//        testDaemon();
//        testJoin1();
//        testJoin2();
//        testJoin3();
//        testSynMethod();
//        testWaitShouldInSynScope();
//        testWaitTime();
//        testWaitAndSleep();
//        testLocalVariable();
//        testYield();
//        testUncaughtExceptionHandler();
//        testDefaultUncaughtExceptionHandler();
//        testAllHandleException();

//        testDirtyRead();
//        testExceptionReleaseLock();

//        testMultiSynJudge();
//        testStringPool();

//        testGoodSuspendResume();
//        testExceptionNotAffectMainThread();

        testWaitNotify1();
    }

    /**
     * 守护线程也叫精灵线程，当程序只剩下守护线程的时候程序就会退出。JVM会在所有的非守护线程（用户线程）执行完毕后退出；
     * main线程是用户线程；仅有main线程一个用户线程执行完毕，不能决定JVM是否退出，也即是说main线程并不一定是最后一个退出的线程。
     * 守护线程的作用：类似在后台静默执行，比如JVM的垃圾回收机制, 这个就是一个守护线程。 而非守护线程则不会。
     * main以及其他创建的线程都是非守护线程
     */
    public static void testDaemon() {
        System.out.println(Thread.currentThread().getName() + ":" + Thread.currentThread().isDaemon());
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + ":" + Thread.currentThread().isDaemon());

            }
        }, "first");
        thread.setDaemon(true);//thread.setDaemon(true)必须在thread.start()之前设置
        thread.start();

        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < 10; i++) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        System.out.println(Thread.currentThread().getName() + ":" + Thread.currentThread().isDaemon());
                    }
                } finally {
                    System.out.println("finally not always run。。。");//Daemon线程不一定执行,不能依靠finally释放资源
                }
            }
        }, "second");
        thread1.setDaemon(true);//打开则程序直接退出，不会等到thread1执行完run方法
        thread1.start();
    }


    //join =================
    //join内部也是调用了wait，监视的对象就是调用join的那个对象，而等待的是当前线程
    //监视对象内部有等待队列，谁监视，若没有获取锁，则进入等待队列


    //等待一个线程执行完再执行。底层就是用了wait+notify(线程结束自动)机制
    public static void testJoin1() throws InterruptedException {
        System.out.println("before testJoin...");

        final Thread subThread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {
                    System.out.println(i);
                }
            }
        }, "threadname");

        subThread.start();
        //优先执行thread，后才执行main,可以注掉这行试试
        subThread.join();//主线程调用thread对象的wait进入thread对象的等待队列,是个同步方法。
        // 当subThread线程执行完后(或非正常死亡时抛出InterruptedException)自动调用notifyall，
        // 所以join中判断thrad是否存活，然后跳出来。
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


    public static void testJoin3() throws InterruptedException {
        System.out.println("before test...");

        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {
                    System.out.println("xxxx==>" + i);
                }
            }
        }, "threadname1");

        final Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {
                    System.out.println("yyyy==>" + i);
                }
            }
        }, "threadname2");


        thread.start();
        thread2.start();

        //1和2线程交互执行，但是main最后
        thread.join();
        thread2.join();

        System.out.println("main stop ");

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

        new Thread(new Runnable() {
            @Override
            public void run() {
                twoSynMethodClass.test2();
            }
        }, "threadname2").start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                twoSynMethodClass.test4();
            }
        }, "threadname3").start();
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

    public static void testWaitAndSleep() {
        final ExecutorService exec = Executors.newFixedThreadPool(4);
        final ReentrantLock lock = new ReentrantLock();

        final Runnable add = new Runnable() {
            public void run() {
                System.out.println("Pre " + lock.toString());
                lock.lock();
                try {
                    //释放锁
//                    final Condition con = lock.newCondition();
//                    con.await(5, TimeUnit.SECONDS);
                    //不释放锁
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    System.out.println("Post " + lock.toString());
                    lock.unlock();
                }
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

    //在适当时机让出当前cpu，给与其他线程执行机会
    private static void testYield() {

        final ExecutorService exec = Executors.newFixedThreadPool(2);

        final Runnable add = new Runnable() {
            public void run() {
                for (int i = 0; i < 30; i++) {
                    if (i == 10) {
                        System.out.println(Thread.currentThread().getName() + "test yield " + i);
                        //让出当前线程，与其他线程一起竞争
                        Thread.yield();
                    }
                }


            }
        };

        exec.submit(add);
        exec.submit(add);

        exec.shutdown();
    }


    //某个异常的默认异常处理器
    public static void testUncaughtExceptionHandler() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("111");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                throw new RuntimeException("you wenti ");

            }
        });

        thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {

                System.out.println("xxxxxx");
                e.printStackTrace();
                //有问题重新连接
//                try {
//                    testUncaughtExceptionHandler();
//                } catch (Exception e1) {
//                    e1.printStackTrace();
//                }
            }
        });

        thread.start();
    }

    //所有线程的默认异常处理器
    //defaultUncaughtExceptionHandler是静态属性，所以设定后全局有效
    public static void testDefaultUncaughtExceptionHandler() {

        MyThread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {

                System.out.println("xxxxxx,message:" + e.getMessage());
            }
        });

        MyThread thread = new MyThread(new Runnable() {
            @Override
            public void run() {
                System.out.println("111");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                throw new RuntimeException("you wenti 1");

            }
        });

        MyThread2 thread2 = new MyThread2(new Runnable() {
            @Override
            public void run() {
                System.out.println("111");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                throw new RuntimeException("you wenti 2");

            }
        });

        thread.start();

        thread2.start();
    }

    //线程设定thread.setUncaughtExceptionHandler则只执行这个
    //若未设定，则使用Thread.setDefaultUncaughtExceptionHandler以及线程组的uncaughtException
    public static void testAllHandleException() {

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                System.out.println("xxxxxx,message:" + e.getMessage());
            }
        });

        ThreadGroupTest.MyThreadGroup myThreadGroup = new ThreadGroupTest.MyThreadGroup("myThreadGroup");

        Thread thread = new Thread(myThreadGroup, new Runnable() {
            @Override
            public void run() {
                System.out.println("111");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                throw new RuntimeException("you wenti ");

            }
        });

//        thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
//            @Override
//            public void uncaughtException(Thread t, Throwable e) {
//                System.out.println("xxxxxx");
//                e.printStackTrace();
//            }
//        });

        thread.start();
    }

    private static class MyThread extends Thread {

        public MyThread(Runnable target) {
            super(target);
        }
    }

    private static class MyThread2 extends Thread {

        public MyThread2(Runnable target) {
            super(target);
        }
    }


    private static void testStart() {
        new Thread() {
            @Override
            public void run() {
                System.out.println("1111");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();//会调用run方法,如果有target则执行target.run,否则执行重写了run这个
    }

    private static void testThis() {
        CountOperate target = new CountOperate();
        Thread thread = new Thread(target);
        thread.setName("A");
        thread.start();
    }

    public static class CountOperate extends Thread {

        public CountOperate() {
            System.out.println("constructor CountOperate");
            System.out.println("Thread.currentThread().getName():" + Thread.currentThread().getName());
            System.out.println("this.getName():" + this.getName());//自动分配名称，只不过是继承了thread当成runnable用！
        }

        @Override
        public void run() {
            System.out.println("run CountOperate");
            System.out.println("Thread.currentThread().getName():" + Thread.currentThread().getName());
            System.out.println("this.getName():" + this.getName());
        }
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
                //t1只要isAlive就会等待在这里
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

            System.out.println("main...sleep before,"+System.currentTimeMillis());
            Thread.sleep(7000);
            System.out.println("main...sleep after,"+System.currentTimeMillis());
        }
    }
}