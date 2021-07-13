package com.wolf.concurrenttest.threadpool;

import com.wolf.concurrenttest.jcip.stdlib.FutureTaskDemo;
import com.wolf.concurrenttest.jcip.threadpool.MyThreadFactory;
import com.wolf.concurrenttest.jcip.threadpool.TimingThreadPool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * shutdown 和shutdownnow区别
 * 前者需要等待所有线程执行完毕，停止线程池，先试图调用w.tryLock()再interrupt
 * 后者直接调用interrupt
 * <p>
 * cpu密集型配置线程数少些，Ncpu+1。减少cpu在每个线程之间的切换
 * io密集型配置线程数多些，2Ncpu。由于都是io等待，cpu使用率很少，多线程执行时遇到io，则cpu切换到另一个线程执行，更好利用cpu
 * <p>
 * 使用有界队列防止内存溢出。
 * <p>
 * executor将任务的提交和任务的执行分离开
 * <p>
 * workQueue.poll(keepAliveTime, TimeUnit.NANOSECONDS)控制大于coresize的线程数量
 * <p>
 * <p>
 * <p>
 * <br/> Created on 2017/5/15 9:25
 *
 * @author 李超
 * @since 1.0.0
 */
public class ExecutorsTest {

    public static void main(String[] args) throws Exception {
//        testShutDownAndNow();
//        testAwaitTermination();
//        testInterruptAwaitTermination();
//        testAwaitTerminationUse();
//        testFeatureSimple();
//        testFeatureAlwaysWait();
        testFeatureWaitTimeOut();
//        testFixedThreadPool();
//        testCachedThreadPool();
//        testMultiFuture();
//        testThreadFactory();
//        testCustomThreadPool();
//        testMonitor();
//        testException();
    }

    /**
     * 由于shutdown需要trylock，所以只能子线程5秒完成后才能关闭
     * shutdownNow直接Interrupt
     */
    private static void testShutDownAndNow() {
        final ExecutorService executorService = Executors.newFixedThreadPool(22);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                for (int x = 0; x < 100; x++) {
                    System.out.println(Thread.currentThread().getName() + " 1111");
                }
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
            }
        });

        System.out.println(" finish shutdown");
//        executorService.shutdown();
        executorService.shutdownNow();//java.lang.InterruptedException: sleep interrupted
    }


    /**
     * awaitTermination
     * Blocks until all tasks have completed execution after a shutdown request,
     * or the timeout occurs, or the current thread is interrupted, whichever happens first.
     */
    private static void testAwaitTermination() {
        final ExecutorService executorService = Executors.newFixedThreadPool(22);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                for (int x = 0; x < 100; x++) {
                    System.out.println(Thread.currentThread().getName() + " 1111");
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(" finish shutdown");
        executorService.shutdown();

        //让线程在这里等待
        try {
            executorService.awaitTermination(2000, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("awaitTermination...");
    }

    /**
     * awaitTermination也可以被终止打断
     */
    private static void testInterruptAwaitTermination() {
        final ExecutorService executorService = Executors.newFixedThreadPool(22);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                for (int x = 0; x < 100; x++) {
                    System.out.println(Thread.currentThread().getName() + " 1111");
                }
            }
        });

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + " before awaitTermination");
                try {
                    executorService.awaitTermination(2000, TimeUnit.HOURS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println(Thread.currentThread().getName() + " after awaitTermination");
            }
        });

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(" finish shutdown");
        executorService.shutdown();
//        executorService.shutdownNow();
    }

    /**
     * 看了awaitTermination说明才知道的用途。。shutdown后等待所有线程完毕/超时/当前线程被中断
     * 原来错误将awaitTermination放到executorService.execute中执行了。。。那所有线程是执行不完的。。
     * <p>
     * 这个例子可能是用来处理executorService线程池shutdown后的后续处理操作
     */
    private static void testAwaitTerminationUse() {
        final ExecutorService executorService = Executors.newFixedThreadPool(22);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                for (int x = 0; x < 100; x++) {
                    System.out.println(Thread.currentThread().getName() + " 1111");
                }
            }
        });

        executorService.shutdown();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(" finish shutdown");

                System.out.println(Thread.currentThread().getName() + " before awaitTermination");
                try {
                    executorService.awaitTermination(1000, TimeUnit.HOURS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + " after awaitTermination");
            }
        }).start();

    }

    private static void testFeatureSimple() throws Exception {
        final ExecutorService executorService = Executors.newFixedThreadPool(22);
        Future<?> submit = executorService.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return "ABC";
            }
        });

        Object o = submit.get();
        System.out.println(o);

        executorService.shutdown();
    }

    private static void testFeatureAlwaysWait() throws Exception {
        final ExecutorService executorService = Executors.newFixedThreadPool(22);
        Future<?> submit = executorService.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                Thread.sleep(10 * 1000);
                return "ABC";
            }
        });

        //一直等
        Object o = submit.get();
        System.out.println(o);

        executorService.shutdown();
    }

    /**
     * 可以解决控制目标方法超时操作
     * 底层用过的unsafe的休眠，如果没有获取锁则进入队列，然后休眠，超时则抛出异常，如果feature执行完则唤醒他
     *
     * @throws Exception
     */
    private static void testFeatureWaitTimeOut() {
        final ExecutorService executorService = Executors.newFixedThreadPool(3, new ThreadFactory() {
            AtomicInteger count = new AtomicInteger();
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "test-"+count.incrementAndGet());
            }
        });

        Future<?> submit = executorService.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                System.out.println("111");
                Thread.sleep(6 * 1000);
                return "ABC";
            }
        });

        //用错单位了。。还跟了下代码以为一直需要等到任务执行完呢，但是一想不对既然有超时应该就有提前停止的可能
//        Object o = submit.get(3000L, TimeUnit.SECONDS);
        //等3秒
        try {
            Object o = submit.get(3L, TimeUnit.SECONDS);//内部抛出TimeoutException
            System.out.println(o);
        } catch (TimeoutException e) {
            e.printStackTrace();
            // task will be cancelled below
        } catch (ExecutionException e) {
            // exception thrown in task; rethrow
            throw FutureTaskDemo.launderThrowable(e.getCause());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // Harmless if task already completed
            boolean cancel = submit.cancel(true);// interrupt if running  注释掉则原线程继续执行
            System.out.println("cancel:"+cancel);
        }

        executorService.shutdown();
    }

    //超时了，是否继续执行
    //若是超时，则调用线程get返回超时，但是执行线程还是会继续，若是执行了cancel(true)就不会执行了，因为内部是worker包含thread，这个
    //会被cancel进行interrupt
    private static void testTimeOutIfContinueRun() {
        final ExecutorService executorService = Executors.newFixedThreadPool(3, new ThreadFactory() {
            AtomicInteger count = new AtomicInteger();
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "test-"+count.incrementAndGet());
            }
        });

        Future<?> submit = executorService.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                System.out.println("111");
                Thread.sleep(6 * 1000);
                System.out.println("2222");
                return "ABC";
            }
        });

        try {
            Object o = submit.get(3L, TimeUnit.SECONDS);//内部抛出TimeoutException
            System.out.println(o);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            boolean cancel = submit.cancel(true);//注释掉则原线程继续执行
            System.out.println("cancel:"+cancel);
        }
    }

    //内部用LinkedBlockingQueue
    private static void testFixedThreadPool() throws Exception {

        final ExecutorService executorService = Executors.newFixedThreadPool(2);
        for (int i = 0; i < 4; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println(Thread.currentThread().getName() + "__xxx");
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        executorService.shutdown();
    }

    //内部用SynchronousQueue的offer永远返回false，一直添加线程
    //因为SynchronousQueue还怕新放任务不被执行。由于是firstTask，所以会被执行。后续的offer都会有已经存在的线程执行poll了
    private static void testCachedThreadPool() throws Exception {

        final ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < 50; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println(Thread.currentThread().getName() + "__xxx");
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        executorService.shutdown();
    }


    private static void testMultiFuture() throws Exception {

        final ExecutorService executorService = Executors.newCachedThreadPool();

        List<Callable<String>> list = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            Callable<String> callable = () -> Thread.currentThread().getName() + "a";
            list.add(callable);
        }
        //执行所有，任何异常都会被忽略，阻塞获取直到所有future执行完返回
        List<Future<String>> futures = executorService.invokeAll(list);
        for (Future<String> future : futures) {
            System.out.println(future.get());
        }

        executorService.shutdown();
    }

    //一开始测试有多个名称一样的，还以为是并发问题，后来一想，哦线程池就是重用的！！，
    // 另外execute是串行的，里面的work也是串行的调用factory的newthread，而且还是atomicinteger
    private static void testThreadFactory() throws Exception {

        final ExecutorService executorService = Executors.newCachedThreadPool(new MyThreadFactory("test-pool-name"));

        for (int i = 0; i < 20; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println(Thread.currentThread().getName());
                }
            });
        }

        executorService.shutdown();
    }

    private static void testCustomThreadPool() throws Exception {

        int size = getPoolSize();

        TimingThreadPool timingThreadPool = new TimingThreadPool((int) (size * 0.8), size + 1);

        for (int i = 0; i < 20; i++) {
            timingThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println(Thread.currentThread().getName());
                }
            });
        }

        timingThreadPool.shutdown();
    }

    private static int getPoolSize() {
        SysInfoAcquirerService sysInfoAcquirerService = new SysInfoAcquirerService();
        sysInfoAcquirerService.init();
        int processor = Runtime.getRuntime().availableProcessors();
        float cpuRate = Float.parseFloat(sysInfoAcquirerService.getCPURate());
        int waitDivideComputeTime = 1 / 2;
        return (int) (processor * cpuRate * (1 + waitDivideComputeTime));
    }

    //也可以重写beforeExecute、afterExecute、terminated方法，监控平均执行时间、最大执行时间、最小执行时间。
    private static void testMonitor() throws Exception {

        int size = getPoolSize();

        TimingThreadPool timingThreadPool = new TimingThreadPool((int) (size * 0.8), size + 1);

        for (int i = 0; i < 20; i++) {
            timingThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(Thread.currentThread().getName());
                }
            });
        }

        System.out.println("timingThreadPool.getTaskCount():" + timingThreadPool.getTaskCount());
        System.out.println("timingThreadPool.getCompletedTaskCount():" + timingThreadPool.getCompletedTaskCount());
        System.out.println("timingThreadPool.getLargestPoolSize():" + timingThreadPool.getLargestPoolSize());
        System.out.println("timingThreadPool.getPoolSize():" + timingThreadPool.getPoolSize());
        System.out.println("timingThreadPool.getActiveWorkerCount():" + timingThreadPool.getActiveCount());

    }

    private static void testException() throws Exception {

        ExecutorService executorService = Executors.newCachedThreadPool();

        //除以0的异常被吃掉了
//        for(int i = 0; i < 5; i++) {
//            int finalI = i;
//            executorService.submit(new Runnable() {
//                @Override
//                public void run() {
//                    int i1 = 100 / finalI;
//                    System.out.println(i1);
//                }
//            });
//        }

        //execute正常打印异常
//        Exception in thread "pool-1-thread-1" java.lang.ArithmeticException: / by zero
//        at com.wolf.concurrenttest.threadpool.ExecutorsTest$17.run(ExecutorsTest.java:433)
//        at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
//        at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
//        at java.lang.Thread.run(Thread.java:748)

        for (int i = 0; i < 5; i++) {
            int finalI = i;
            Runnable command = new Runnable() {//打印异常
                @Override
                public void run() {
                    int i1 = 100 / finalI;
                    System.out.println(i1);
                }
            };

            executorService.execute(command);
        }

        //需要通过get才能获取到异常信息
        //        Exception in thread "main" java.util.concurrent.ExecutionException: java.lang.ArithmeticException: / by zero
//        at java.util.concurrent.FutureTask.report(FutureTask.java:122)
//        at java.util.concurrent.FutureTask.get(FutureTask.java:192)
//        at com.wolf.concurrenttest.threadpool.ExecutorsTest.testException(ExecutorsTest.java:459)
//        at com.wolf.concurrenttest.threadpool.ExecutorsTest.main(ExecutorsTest.java:47)
//        Caused by: java.lang.ArithmeticException: / by zero
//        at com.wolf.concurrenttest.threadpool.ExecutorsTest$17.run(ExecutorsTest.java:452)
//        at java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:511)
//        at java.util.concurrent.FutureTask.run(FutureTask.java:266)
//        at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
//        at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
//        at java.lang.Thread.run(Thread.java:748)

        //这个似乎可以打印出提交位置
//        for (int i = 0; i < 5; i++) {
//            int finalI = i;
//            Runnable task = new Runnable() {
//                @Override
//                public void run() {
//                    int i1 = 100 / finalI;
//                    System.out.println(i1);
//                }
//            };
//
//            Future<?> submit = executorService.submit(task);
//
//            Object o = submit.get();
//            System.out.println(o);
//        }


//        TraceThreadPool traceThreadPool = new TraceThreadPool(5,10,60,TimeUnit.SECONDS,new ArrayBlockingQueue<Runnable>(20));
//        for (int i = 0; i < 5; i++) {
//            int finalI = i;
//            Runnable task = new Runnable() {
//                @Override
//                public void run() {
//                    int i1 = 100 / finalI;
//                    System.out.println(i1);
//                }
//            };
//
//            traceThreadPool.execute(task);
//        }
//
//        traceThreadPool.shutdown();

    }

    //大家都使用线程池处理数据，但是发生异常结果了，却不知道哪里提交的，何从查找呢？可以通过基本日志查询runnable类，然后再定位，但是若大家公用runnable就不好查了。
    //异常时。看到任务从哪里提交的。
    private static class TraceThreadPool extends ThreadPoolExecutor {
        public TraceThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        }

        @Override
        public void execute(Runnable task) {
            super.execute(wrap(task, clientTrace()));
        }

        private Exception clientTrace() {
            return new Exception("Client stack trace");
        }

        //包装一层runnable，执行run方法时，目标run若有异常，则先打印客户端异常堆栈，直到是哪里提交的任务
        private Runnable wrap(Runnable task, Exception clientException) {
            return new Runnable() {
                @Override
                public void run() {
                    try {
                        task.run();
                    } catch (Exception e1) {
                        clientException.printStackTrace();
                        throw e1;
                    }
                }
            };
        }

        @Override
        public Future<?> submit(Runnable task) {
            return super.submit(wrap(task, clientTrace()));
        }
    }
}
