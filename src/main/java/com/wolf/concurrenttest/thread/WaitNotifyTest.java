package com.wolf.concurrenttest.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Description:
 * 锁有两个队列，就绪队列(notify,锁释放了)，阻塞队列(wait,等待获取锁)
 * wait,执行状态进入等待队列，等待状态
 * notify，通知等待队列中*一个*线程进入就绪队列，可运行状态
 * runnable和running状态可互换，看cpu分配时间片执行
 * <br/> Created on 2017/4/27 22:00
 *
 * @author 李超
 * @since 1.0.0
 */
public class WaitNotifyTest {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //testBase();
        //testFirstNodity();
        //testFutureNodity();
        testCondition();
        //testCondition2();
        //testLatch();
        //testWhileJudge();
    }

    private static void testBase() {
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                for (int x = 0; x < 100; x++) {
                    synchronized (WaitNotifyTest.class) {
                        System.out.println("enter synchronized,thread:" + Thread.currentThread().getName());
                        for (int i = 0; i < 10; i++) {
                            System.out.println(Thread.currentThread().getName() + " " + i);
                            try {
                                Thread.sleep(250);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        WaitNotifyTest.class.notifyAll();//不会释放锁，要等后续所有代码执行完退出synchronized或者当前线程调用了wait，释放锁，其他wait线程才能获取锁
                        System.out.println("thread :" + Thread.currentThread().getName() + " notifyall");
                        try {
                            Thread.sleep(4000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        try {
                            WaitNotifyTest.class.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        new WaitNotifyTest().mainThread();
    }

    private void mainThread() {
        for (int x = 0; x < 100; x++) {
            synchronized (WaitNotifyTest.class) {
                System.out.println("enter synchronized,thread:" + Thread.currentThread().getName());
                try {
                    //this.wait();,等待的是对象上的锁，这里进来的主线程获取的是WaitNotifyTest.class的锁，
                    // 但是this.wait()调用的是当前对象的锁,所以出现了IllegalMonitorStateException
                    WaitNotifyTest.class.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < 20; i++) {
                    System.out.println(Thread.currentThread().getName() + " " + i);
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                WaitNotifyTest.class.notify();//注意，若是多个wait，只能唤醒一个，那个线程执行完若无其他notify那么剩余的wait线程一直等待。
                System.out.println("thread :" + Thread.currentThread().getName() + " notify");
            }
        }
    }

    //先用notify则wait不会再被唤醒
    private static void testFirstNodity() {
        Object lock = new Object();

        synchronized (lock) {
            System.out.println("begin notify");
            lock.notify();
            System.out.println("after notify");
        }

        new Thread(() -> {
            synchronized (lock) {
                try {
                    System.out.println("begin wati");
                    lock.wait();
                    System.out.println("after wati");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // 解决testFirstNodity的问题
    private static void testFutureNodity() throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        Future future = executorService.submit(() -> {
            try {
                System.out.println("begin wati");
                TimeUnit.SECONDS.sleep(1);
                System.out.println("after wati");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 111;
        });

        TimeUnit.SECONDS.sleep(5);
        executorService.shutdown();

        Object o = future.get();
        System.out.println(o);
    }

    // todo 先notifyAll然后再await不行。
    private static void testCondition() throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        ReentrantLock lock = new ReentrantLock();

        Condition condition = lock.newCondition();

        executorService.execute(() -> {
            System.out.println("begin wati");
            lock.lock();
            condition.signalAll();
            lock.unlock();
            System.out.println("after wati");
        });

        TimeUnit.SECONDS.sleep(5);
        lock.lock();
        condition.await();
        lock.unlock();

        System.out.println(1111);
    }

    private static volatile int a = 0;

    // 需要关联上state这样happen-before就能保证数据一定可见，不论谁先执行
    private static void testCondition2() throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        ReentrantLock lock = new ReentrantLock();

        Condition condition = lock.newCondition();

        executorService.execute(() -> {
            System.out.println("begin wati");
            lock.lock();
            a = 1;
            condition.signalAll();
            lock.unlock();
            System.out.println("after wati");
        });

        lock.lock();
        while (a == 0) {
            condition.await();
        }
        lock.unlock();

        executorService.shutdown();
        System.out.println(1111);
    }

    // 解决testFirstNodity的问题
    private static void testLatch() throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        CountDownLatch latch = new CountDownLatch(1);

        executorService.execute(() -> {
            System.out.println("begin wati");
            latch.countDown();
            System.out.println("after wati");
        });

        TimeUnit.SECONDS.sleep(5);
        latch.await();

        executorService.shutdown();
        System.out.println(1111);
    }

    private static List<Integer> list = new ArrayList<>();

    //使用while进行条件判断，不然若被唤醒但是其他线程获得锁操作后，当前线程再操作，条件不再符合就应该等待不应该执行了。
    private static void testWhileJudge() {
        Object lock = new Object();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (lock) {
                    System.out.println("notifyAll begin:" + Thread.currentThread().getName());
                    list.add(1);
                    lock.notifyAll();
                }
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (lock) {
                    try {
                        while (list.size() == 0) {
                            System.out.println("wait begin:" + Thread.currentThread().getName());
                            lock.wait();
                        }
                        list.remove(0);
                        System.out.println("remove success:" + Thread.currentThread().getName());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (lock) {
                    try {
                        while (list.size() == 0) {
                            System.out.println("wait begin:" + Thread.currentThread().getName());
                            lock.wait();
                        }
                        list.remove(0);
                        System.out.println("remove success:" + Thread.currentThread().getName());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        thread.start();
    }
}
