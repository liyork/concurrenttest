package com.wolf.concurrenttest.threadpool;

import com.wolf.concurrenttest.common.TakeTimeUtils;

import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Description:所有线程调用cyclicBarrier.await();，等待，当达到指定数量时，大家一起执行。
 * 可以重复使用，所有完成时触发barrierAction
 *
 * catch (BrokenBarrierException e) {e.printStackTrace();}，若发生异常则打印信息后还可能向下执行，注意整体已执行是否被破坏是否要直接return？
 }
 * <br/> Created on 2017/6/25 9:38
 *
 * @author 李超
 * @since 1.0.0
 */
public class CyclicBarrierTest {

    public static void main(String[] args) throws BrokenBarrierException, InterruptedException {
//        testBase();
        testSimulateCountDownLatch();
//        testReset();
//        testOtherMethod();
//        testTwoAwaitAndInterrupt();
    }

    private static void testBase() {
        ExecutorService executorService = Executors.newFixedThreadPool(4);

        final CyclicBarrier cyclicBarrier = new CyclicBarrier(4, new Runnable() {
            @Override
            public void run() {
                System.out.println("all is done");//所有线程都调用await后执行这里再执行他们await后的代码
            }
        });

        final Random random = new Random();
        for (int i = 0; i < 4; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println(Thread.currentThread().getName() + " is begin running...");
                    int sleepTime = random.nextInt(10000);
                    try {
                        System.out.println(Thread.currentThread().getName() + " wait " + sleepTime);
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    try {
                        cyclicBarrier.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (BrokenBarrierException e) {
                        e.printStackTrace();
                    }
                    System.out.println(Thread.currentThread().getName() + " is operation...");
                    TakeTimeUtils.simulateLongTimeOperation(500000);
                    System.out.println(Thread.currentThread().getName() + " is end running...");
                }
            });
        }

        System.out.println(" main end..");

        executorService.shutdown();
    }

    /**
     * 每个线程执行完操作后调用await，等调用wait数量够了后，执行汇总操作的runnable,然后执行wait后的剩余方法
     */
    private static void testSimulateCountDownLatch() throws BrokenBarrierException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(4);

        final CyclicBarrier cyclicBarrier = new CyclicBarrier(5, new Runnable() {
            @Override
            public void run() {
                System.out.println("compute all task result");
            }
        });

        final Random random = new Random();
        for (int i = 0; i < 4; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println(Thread.currentThread().getName() + " is begin running...");
                    int sleepTime = random.nextInt(10000);
                    try {
                        System.out.println(Thread.currentThread().getName() + " wait " + sleepTime);
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    System.out.println(Thread.currentThread().getName() + " is operation...");
                    TakeTimeUtils.simulateLongTimeOperation(250000);
                    System.out.println(Thread.currentThread().getName() + " is end running...");

                    try {
                        cyclicBarrier.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (BrokenBarrierException e) {
                        e.printStackTrace();
                    }

                }
            });
        }

        cyclicBarrier.await();
        System.out.println("main..");

        executorService.shutdown();
    }

    private static void testReset() throws BrokenBarrierException, InterruptedException {
        final CyclicBarrier cyclicBarrier = new CyclicBarrier(3);
        AtomicInteger atomicInteger = new AtomicInteger(3);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (atomicInteger.get() != 0) {
                    try {
                        System.out.println(Thread.currentThread().getName() + " atomicInteger.decrementAndGet()");
                        atomicInteger.decrementAndGet();
                        cyclicBarrier.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (BrokenBarrierException e) {
                        //e.printStackTrace();
                        atomicInteger.incrementAndGet();
                    }
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                int tryCount = 0;
                while (atomicInteger.get() != 0) {
                    try {
                        int a = 1;
                        Thread.sleep(2000);
                        System.out.println(Thread.currentThread().getName() + " is begin running...");
                        if (a == 1 && tryCount != 3) {
                            tryCount++;
                            throw new Exception("try exception");
                        }
                        atomicInteger.decrementAndGet();
                        cyclicBarrier.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (BrokenBarrierException e) {
                        e.printStackTrace();
                        atomicInteger.incrementAndGet();
                    } catch (Exception e) {
                        //e.printStackTrace();
                        System.out.println("occur exception " + e.getMessage());
                        cyclicBarrier.reset();
                    }
                }
            }
        }).start();

        while (atomicInteger.get() != 0) {
            try {
                System.out.println(Thread.currentThread().getName() + " atomicInteger.decrementAndGet()");
                atomicInteger.decrementAndGet();
                cyclicBarrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                //e.printStackTrace();//遇到其他线程执行reset则抛出异常
                atomicInteger.incrementAndGet();
            }
        }

        System.out.println("abc...");
    }

    private static void testOtherMethod() throws InterruptedException {

        final CyclicBarrier cyclicBarrier = new CyclicBarrier(5);

        final Random random = new Random();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + " is begin running...");
                int sleepTime = random.nextInt(10000);
                try {
                    System.out.println(Thread.currentThread().getName() + " wait " + sleepTime);
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    //e.printStackTrace();
                }

                try {
                    cyclicBarrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + " is operation...");
                TakeTimeUtils.simulateLongTimeOperation(500000);
                System.out.println(Thread.currentThread().getName() + " is end running...");

            }
        });
        thread.start();
        thread.interrupt();

        Thread.sleep(2000);
        System.out.println(" cyclicBarrier.getNumberWaiting().." + cyclicBarrier.getNumberWaiting());
        System.out.println("cyclicBarrier.isBroken().."+cyclicBarrier.isBroken());
    }

    private static void testTwoAwaitAndInterrupt() {
        final CyclicBarrier cyclicBarrier = new CyclicBarrier(5,new BarrierAction() );

        final Random random = new Random();

        for (int i = 0; i < 5; i++) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {

                    try {
                        System.out.println(Thread.currentThread().getName() + " is await...");
                        cyclicBarrier.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return;
                    } catch (BrokenBarrierException e) {
                        e.printStackTrace();
                        return;
                    }

                    System.out.println(Thread.currentThread().getName() + " is begin running...");
                    int sleepTime = random.nextInt(10000);

                    try {
                        System.out.println(Thread.currentThread().getName() + " sleep " + sleepTime);
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        //e.printStackTrace();
                    }

                    try {
                        System.out.println(Thread.currentThread().getName() + " is finish...");
                        cyclicBarrier.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (BrokenBarrierException e) {
                        e.printStackTrace();
                    }

                }
            });
            thread.start();
            if (i == 4) {
                //当前线程产生InterruptedException，其他线程产生BrokenBarrierException
                //失败了，收集不齐所有了，那么都不要等待了。
                thread.interrupt();
            }
        }

    }

    //循环使用
    static class BarrierAction implements Runnable {

        boolean flag = false;

        @Override
        public void run() {
            if (flag) {
                System.out.println("执行完成。");
            } else {
                flag = true;
                System.out.println("集合完成。");
            }
        }
    }

}
