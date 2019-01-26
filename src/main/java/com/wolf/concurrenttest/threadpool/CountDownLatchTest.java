package com.wolf.concurrenttest.threadpool;

import com.wolf.concurrenttest.common.TakeTimeUtils;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * <p> Description:
 * <p/>
 * Date: 2015/9/17
 * Time: 14:41
 *
 * @author 李超
 * @version 1.0
 * @since 1.0
 */
public class CountDownLatchTest {

    public static void main(String[] args) throws InterruptedException {
//        testBseFunction();
//        testBaseFunction2();
//        testAwaitTimeout();
        testReduceOne();
    }

    private static void testBseFunction() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(1);

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                countDownLatch.countDown();
            }
        });

        System.out.println(Thread.currentThread().getName() + " before await");
        countDownLatch.await();
        System.out.println(Thread.currentThread().getName() + " end await");

        executorService.shutdown();
    }

    public static void testBaseFunction2() throws InterruptedException {
        int nThreads = 3;
        final Runnable task = new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + " is running...");
                TakeTimeUtils.simulateLongTimeOperation(5000000);
            }
        };
        final CountDownLatch startGate = new CountDownLatch(1);
        final CountDownLatch endGate = new CountDownLatch(nThreads);

        for(int i = 0; i < nThreads; i++) {
            Thread t = new Thread() {
                public void run() {
                    try {
                        startGate.await();
                        try {
                            task.run();
                        } finally {
                            endGate.countDown();
                        }
                    } catch (InterruptedException ignored) {
                    }
                }
            };
            t.start();
        }

        long start = System.nanoTime();
        startGate.countDown();//一起执行
        endGate.await();//最后收集信息
        long end = System.nanoTime();

        System.out.println(end - start);
    }

    private static void testAwaitTimeout() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(1);

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        //等5秒,5秒内有调用countDownLatch.countDown()，则true，否则false
        boolean await = countDownLatch.await(5l, TimeUnit.SECONDS);
        if(await) {
            System.out.println("xxx");
        } else {
            System.out.println("yyy");
        }

        executorService.shutdown();
    }

    private static void testReduceOne() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        final CountDownLatch countDownLatch = new CountDownLatch(1);

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                countDownLatch.countDown();
            }
        });
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                countDownLatch.countDown();//多countDown一次没有关系。。tryReleaseShared返回false，但是countDown不关心结果
            }
        });

        boolean await = countDownLatch.await(5l, TimeUnit.SECONDS);
        if(await) {
            System.out.println("xxx");
        } else {
            System.out.println("yyy");
        }

        executorService.shutdown();
    }


}
