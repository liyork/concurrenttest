package com.wolf.concurrenttest.tooltest;

import com.wolf.concurrenttest.common.TakeTimeUtils;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ReentrantLock的加锁方法Lock()提供了无条件地轮询获取锁的方式，如果过程中有人中断，
 * 则仅仅就是改变了线程的中断标志位，不影响获取锁，但是lock方法内如果有等待则立即抛出异常。
 * lockInterruptibly()提供了可中断的锁获取方式。
 * <p>
 * synchronized原语和ReentrantLock在一般情况下没有什么区别，但是在非常复杂的同步应用中，请考虑使用ReentrantLock，特别是遇到下面2种需求的时候。
 * 1.某个线程在等待一个锁的控制权的这段时间需要中断
 * 2.需要分开处理一些wait-notify，ReentrantLock里面的Condition应用，能够控制notify哪个线程
 * 3.具有公平锁功能，每个到来的线程都将排队等候
 * <p>
 * reentrant语义：可重入，即获取锁的线程当进入还需要同步锁的方法或者递归，不需要再次竞争，直接进入，不然如果不可重入则还需要获取锁，
 * 则由于之前获取过了，永远等待
 * <p>
 * 不公平的语义就是获取锁时，不管以前有没有人排队，自己先试试获取锁，没有获取则排队。对于已经存在队列中的线程来说一个链也算是公平
 * 公平的语义是获取锁时，看看前面如果有人排队，则自己直接进入队尾
 *
 * 公平锁保证了顺序，但是上下文切换频繁，性能不好。非公平锁减少上下文频繁切换，高吞吐。
 *
 * condition就是操作conditionobject中的node和abstractqueeuSynchronizer中的node关系。
 *
 * 队列中是否有数据
 * h != t && ((s = h.next) == null || s.thread != Thread.currentThread());
 * 队列不为空前提
 * 1.head.next指向null也就是正在连接下一个
 * 2.头结点不是当前线程
 *
 * lock就是try然后不行进入队列等待唤醒，被唤醒时若前面是head则进行trylock，否则其他被意外唤醒的继续等待
 * unlock就是把后继节点unpark
 *
 * fast path of enq
 * 获取到锁的节点是head
 * shouldParkAfterFailedAcquire对之前的cancel的node排除
 * 进入队列设定prenode为Node.SIGNAL
 * if cancelled or apparently null,traverse backwards from tail to find the actual non-cancelled successor
 * 释放锁时，设定head的waitStatus=0，将head之后的等待节点unpark，被唤醒的node会竞争，取胜的将自己更新为head，失败则同lock逻辑
 *
 * 公平锁上锁时先看状态和队列，都可以则竞争否则直接进入队列
 *
 * 使用lockInterruptibly响应interrupt，内部抛出异常中断。
 *
 * locksupport应该不能保证可见性，而是由于内部的state是volatile和操作lock和unlock的happen-before加上的数据可见性，
 * 无论是成功获取到锁还是成功释放掉锁，都会先读取state变量的值，再进行修改。
 * 释放锁修改state时之前的任何操作对获取锁操作state之后的都可见
 * A write volatile happens-before B read volatile happens-before B
 *
 * CAS同时具有volatile读和volatile写的内存语义
 * 编译器不会对volatile读与volatile读后面的任意内存操作重排序；
 * 编译器不会对volatile写与volatile写前面的任意内存操作重排序。
 * 组合这两个条件，意味着为了同时实现volatile读和volatile写的内存语义，编译器不能对CAS与CAS前面和后面的任意内存操作重排序。
 * 底层通过硬件的lock锁定内存与内存失效机制
 *
 * <br/> Created on 2017/2/4 13:44
 *
 * @author 李超
 * @since 1.0.0
 */
public class ReentrantLockTest {

    static ReentrantLock reentrantLock = new ReentrantLock();

    public static void main(String[] args) throws InterruptedException {
        testInterruptLock(true);
//        testInterruptLock(false);
//        testTryLock(true);
//        testTryLock(false);

//        lock();
//        lockInterrupt();
//        lockInterrupt2();
//        lockInterruptUnLock();
//        testReentrant();
//        testNormalLock();

//        testHoldCount();
//        testQueueLength();
//        testWaitQueueLength();
//        testWaitCondition();
//        testAwaitUninterruptibly();

        lockShare();
    }

    public static void testInterruptLock(final boolean isCanInterrupt) throws InterruptedException {

        final ReentrantLockTest reentrantLockTest = new ReentrantLockTest();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                reentrantLockTest.test(isCanInterrupt);
            }
        });
        thread.start();

        Thread.sleep(1000);

        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                reentrantLockTest.test(isCanInterrupt);
            }
        });
        thread2.start();

        //2秒后中断thread2
        Thread.sleep(2000);
        thread2.interrupt();
//        thread.interrupt();//想试试lockInterruptibly是不是对于正在获取的锁有中断效果，答案是不能，也对，既然都获取锁了，能不能被中断，只能看锁定后的代码了，锁定本身事件已经完事了。
    }

    private void test(boolean isCanInterrupt) {
        System.out.println(Thread.currentThread().getName() + "_lockInterruptibly...");
        try {
            if (isCanInterrupt) {
                //一直试图获取lock，期间可被打断
                reentrantLock.lockInterruptibly();
            } else {
                //一直阻塞，直到能上锁
                reentrantLock.lock();
            }

            System.out.println(Thread.currentThread().getName() + "_locked...");
            System.out.println("simulateLongTimeOperation...");
            TakeTimeUtils.simulateLongTimeOperation(100000000);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reentrantLock.isHeldByCurrentThread()) {
                reentrantLock.unlock();
                System.out.println(Thread.currentThread().getName() + "_unlock...");
            }
        }
    }


    /**
     * trylock 用途
     * a、用在定时任务时，如果任务执行时间可能超过下次计划执行时间，确保该有状态任务只有一个正在执行，忽略重复触发。
     * b、用在界面交互时点击执行较长时间请求操作时，防止多次点击导致后台重复执行（忽略重复触发）。
     * <p>
     * trylock内部实现：如果未获取锁则排队并且调用底层unsafe进行休眠指定时间，别打断或者锁释放notify了或者时间到了就会醒来
     *
     * @param isWait
     * @throws InterruptedException
     */
    public static void testTryLock(final boolean isWait) throws InterruptedException {

        final ReentrantLockTest reentrantLockTest = new ReentrantLockTest();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                reentrantLockTest.test2(isWait);
            }
        });
        thread.start();

        Thread.sleep(1000);

        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                reentrantLockTest.test2(isWait);
            }
        });
        thread2.start();

        //2秒后中断thread2
        Thread.sleep(2000);
//        thread2.interrupt();
    }

    private void test2(boolean isWait) {
        System.out.println(Thread.currentThread().getName() + "_try lock...");
        try {
            boolean isLock;
            if (isWait) {
                //tryLock也支持响应中断
                isLock = reentrantLock.tryLock(5, TimeUnit.SECONDS);//试图获取，失败则自旋或者等待时长后再获取
                System.out.println("wait 5 second not get lock");
            } else {
                isLock = reentrantLock.tryLock();//直接获取失败则立即返回false
            }

            if (isLock) {
                System.out.println(Thread.currentThread().getName() + "_locked...");
                System.out.println("simulateLongTimeOperation...");
                TakeTimeUtils.simulateLongTimeOperation(120000000);
            } else {
                System.out.println(Thread.currentThread().getName() + " not get lock");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reentrantLock.isHeldByCurrentThread()) {
                reentrantLock.unlock();
                System.out.println(Thread.currentThread().getName() + "_unlock...");
            }
        }
    }

    public static void lock() throws InterruptedException {
        final Lock lock = new ReentrantLock();
        //主线程上锁了
        lock.lock();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //子线程lock是会一直等待，而且lock时对interrupt不感冒
                System.out.println("run begin lock..");
                lock.lock();
                System.out.println("run after lock..");
            }
        });
        thread.start();
        Thread.sleep(2000);
        thread.interrupt();
    }

    public static void lockShare() throws InterruptedException {
        final Lock lock = new ReentrantLock(true);
        //主线程上锁了
        lock.lock();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //子线程lock是会一直等待，对interrupt不感冒
                lock.lock();
            }
        });
        thread.start();
        Thread.sleep(2000);
        thread.interrupt();
    }

    /**
     * 可中断锁定状态
     * 这种情况主要用于取消某些操作对资源的占用
     *
     * @throws InterruptedException
     */
    public static void lockInterrupt() throws InterruptedException {
        final Lock lock = new ReentrantLock();
        //主线程上锁了
        lock.lock();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //子线程lock可以被打断
                    lock.lockInterruptibly();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        Thread.sleep(2000);
        thread.interrupt();
    }

    //先interrupt后lockInterruptibly
    public static void lockInterrupt2() throws InterruptedException {
        final Lock lock = new ReentrantLock();
        lock.lock();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
//                    Thread.sleep(2000);
                    System.out.println(Thread.currentThread().getName() + ":TakeTimeUtils.simulateLongTimeOperation..");
                    TakeTimeUtils.simulateLongTimeOperation(8000000);
                    lock.lockInterruptibly();
                    System.out.println(Thread.currentThread().getName() + ":locked");//不会执行
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        thread.interrupt();
    }

    public static void lockInterruptUnLock() throws InterruptedException {

        final Lock lock = new ReentrantLock();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    lock.lockInterruptibly();
                    System.out.println(Thread.currentThread().getName() + "_get lock");
                } catch (InterruptedException e) {
                    System.out.println("Thread name " + Thread.currentThread().getName() + " is interrupted!");
                }
            }
        });

        lock.lock();
        try{
            thread.start();
            Thread.sleep(2000);
        }finally {
            lock.unlock();
        }
    }

    /**
     * 每次重入不会引起线程抢夺，保证当前获取锁的线程执行
     *
     * @throws InterruptedException
     */
    public static void testReentrant() throws InterruptedException {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                get1();
            }
        });
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                get1();
            }
        });
        Thread thread3 = new Thread(new Runnable() {
            @Override
            public void run() {
                get1();
            }
        });

        thread.start();
        thread2.start();
        thread3.start();
    }

    private static void get1() {
        reentrantLock.lock();
        try {
            System.out.println("get1_" + Thread.currentThread().getName());
            Thread.sleep(2000);
            get2();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            reentrantLock.unlock();
        }
        //一开始放这里，thread1上面释放锁了，然后和其他俩线程一起竞争。。。错了
//        get2();
    }

    private static void get2() {
        reentrantLock.lock();
        try {
            System.out.println("get2_" + Thread.currentThread().getName());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            reentrantLock.unlock();
        }
    }

    /**
     * 一般使用
     * 本来想测测源码中的是否中断当前线程，原来看错了，是由于if没有{}导致看错了。。
     *
     * @throws InterruptedException
     */
    public static void testNormalLock() throws InterruptedException {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                testLockMethod();
            }
        });
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                testLockMethod();
            }
        });

        thread.start();
        thread2.start();
    }

    private static void testLockMethod() {
        reentrantLock.lock();
        try {
            System.out.println("testLockMethod_" + Thread.currentThread().getName());
            System.out.println("isInterrupted_" + Thread.currentThread().isInterrupted());
            Thread.sleep(10000);
            System.out.println("testLockMethod_" + Thread.currentThread().getName());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            reentrantLock.unlock();
        }
    }

    //锁被持有的线程数量
    private static void testHoldCount() {
        reentrantLock.lock();
        try {
            System.out.println("holdcount:" + reentrantLock.getHoldCount());
            testHoldCount1();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            reentrantLock.unlock();
        }
    }

    private static void testHoldCount1() {
        reentrantLock.lock();
        try {
            System.out.println("holdcount:" + reentrantLock.getHoldCount());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            reentrantLock.unlock();
        }
    }

    //等待获取锁的数量
    private static void testQueueLength() {
        try {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    reentrantLock.lock();
                    try {
                        System.out.println(Thread.currentThread().getName() + " enter");
                        Thread.sleep(Integer.MAX_VALUE);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        reentrantLock.unlock();
                    }
                }
            };

            for (int i = 0; i < 5; i++) {
                new Thread(runnable).start();
            }

            Thread.sleep(2000);
            System.out.println("queueLength:" + reentrantLock.getQueueLength());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //有无等待获取锁的队列
    private static void testWaitQueueLength() {
        try {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    reentrantLock.lock();
                    try {
                        System.out.println(Thread.currentThread().getName() + " enter");
                        Thread.sleep(Integer.MAX_VALUE);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        reentrantLock.unlock();
                    }
                }
            };

            Thread thread = null;
            for (int i = 0; i < 5; i++) {
                thread = new Thread(runnable);
                thread.start();
            }

            Thread thread2 = new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("sleep 50000...");
                    try {
                        Thread.sleep(50000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread2.start();

            Thread.sleep(2000);
            System.out.println("hasQueuedThread:" + reentrantLock.hasQueuedThread(thread));
            System.out.println("hasQueuedThread:" + reentrantLock.hasQueuedThread(thread2));
            System.out.println("hasQueuedThreads:" + reentrantLock.hasQueuedThreads());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //等待条件的队列
    private static void testWaitCondition() {
        Condition condition = reentrantLock.newCondition();
        try {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    reentrantLock.lock();//先取锁再在某个条件等待，最后释放锁
                    try {
                        System.out.println(Thread.currentThread().getName() + " enter");
                        condition.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        reentrantLock.unlock();
                    }
                }
            };

            Thread thread = null;
            for (int i = 0; i < 5; i++) {
                thread = new Thread(runnable);
                thread.start();
            }

            reentrantLock.lock();
            try {
                Thread.sleep(2000);
                System.out.println("hasWaiters:" + reentrantLock.hasWaiters(condition));
                System.out.println("getWaitQueueLength:" + reentrantLock.getWaitQueueLength(condition));
            }finally {
                reentrantLock.unlock();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //不受中断影响
    private static void testAwaitUninterruptibly() {
        Condition condition = reentrantLock.newCondition();
        try {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    Calendar instance = Calendar.getInstance();
                    instance.add(Calendar.SECOND,15);
                    reentrantLock.lock();
                    try {
                        System.out.println(Thread.currentThread().getName() + " begin awaitUninterruptibly");
//                        condition.await();
                        condition.awaitUntil(instance.getTime());//15s后无人唤醒则自己醒，或有人唤醒
//                        condition.awaitUninterruptibly();//不被中断
                        System.out.println(Thread.currentThread().getName() + " end awaitUninterruptibly");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        reentrantLock.unlock();
                    }
                }
            };

            Thread thread = new Thread(runnable);
            thread.start();

            //测试临时关闭
//            Thread.sleep(2000);
//            thread.interrupt();
//            System.out.println(Thread.currentThread().getName() +" interrupt");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
