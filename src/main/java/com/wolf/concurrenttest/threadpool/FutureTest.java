package com.wolf.concurrenttest.threadpool;

import java.util.concurrent.*;

/**
 * <p> Description:future可实现超时，可被打断.
 * Callable只是用来被调用的,submit时会被封装成FutureTask，(Runnable, Future)可运行可获取，
 * <p/>
 * Date: 2016/6/23
 * Time: 9:01
 *
 * @author 李超
 * @version 1.0
 * @since 1.0
 */
public class FutureTest {

    public static void main(String[] args) {

//        testCommon();
//        testInterrupted();
//        testRunnableResult();
        testGetTimeout();
    }

    private static void testCommon() {
        ExecutorService exec = Executors.newCachedThreadPool();

        testTask(exec, 15); // 成功
        testTask(exec, 5); //  失败

        exec.shutdown();
    }

    public static void testTask(ExecutorService exec, int timeout) {
        MyTask task = new MyTask();
        Future<Boolean> future = exec.submit(task);
        Boolean taskResult = null;
        String failReason = null;
        try {
            // 等待计算结果，最长等待timeout秒，timeout秒后中止任务
            taskResult = future.get(timeout, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            failReason = "InterruptedException";
        } catch (ExecutionException e) {
            failReason = "ExecutionException";
        } catch (TimeoutException e) {
            failReason = "TimeoutException";
            exec.shutdownNow();
        }

        System.out.println("\ntaskResult : " + taskResult);
        System.out.println("failReason : " + failReason);
    }

    //无返回结果，一定要是Callable
    public static void testRunnableResult() {
        ExecutorService exec = Executors.newCachedThreadPool();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                System.out.println("in running ...");
            }
        };
        Future<?> future = exec.submit(runnable);
        Object o = null;
        try {
            o = future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        System.out.println(o);//null
        System.out.println(future.isDone());//true
    }

    private static void testInterrupted() {
        ExecutorService exec = Executors.newCachedThreadPool();
        final MyTask task = new MyTask();

        //准备终止目标任务的线程
        exec.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                task.stop();
            }
        });


        Future<Boolean> future = exec.submit(task);
        try {
            Boolean taskResult = future.get();
            System.out.println(taskResult);
        } catch (InterruptedException e) {
            //这部分应该视具体情况而定
            // Re-assert the thread's interrupted status
            Thread.currentThread().interrupt();
            // We don't need the result, so cancel the task too
            future.cancel(true);
        } catch (ExecutionException e) {
            System.out.println("ExecutionException");
            System.out.println(Thread.currentThread().getName());
            e.printStackTrace();
        }

        exec.shutdownNow();
    }

    //等待一定时间超时则抛出异常或者被interrupt，或正常返回。
    public static void testGetTimeout() {
        ExecutorService exec = Executors.newCachedThreadPool();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(600000000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("in running ...");
            }
        };
        Future<?> future = exec.submit(runnable);
        Object o = null;
        try {
            o = future.get(3000,TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        System.out.println(o);//null
        System.out.println(future.isDone());//true
    }
}