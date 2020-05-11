package com.wolf.concurrenttest.thread;

import java.util.concurrent.TimeUnit;

/**
 * Description:通过回调，感知有异常
 * <p>
 * 线程遇到异常，调用getUncaughtExceptionHandler
 * <br/> Created on 2019-01-27
 *
 * @author 李超
 * @since 1.0.0
 */
public class UncaughtExceptionHandlerTest {

    public static void main(String[] args) {

//        testDefaultUncaughtExceptionHandler();
        testEmptyExceptionHandler();
    }

    //所有线程的默认异常处理器
    //defaultUncaughtExceptionHandler是静态属性，所以设定后全局有效
    public static void testDefaultUncaughtExceptionHandler() {

        Thread.setDefaultUncaughtExceptionHandler(
                (t, e) -> {
                    System.out.println(t.getName() + "_message:" + e.getMessage());
                    e.printStackTrace();
                });

        MyThread thread = new MyThread(
                () -> {
                    System.out.println("111");
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    throw new RuntimeException("run error1.");
                });

        MyThread2 thread2 = new MyThread2(
                () -> {
                    System.out.println("222");
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    throw new RuntimeException("run error2.");

                });

        thread.start();
        thread2.start();
    }

    private static void testEmptyExceptionHandler() {

        ThreadGroup mainGroup = Thread.currentThread().getThreadGroup();
        System.out.println(mainGroup);
        System.out.println(mainGroup.getParent());
        System.out.println(mainGroup.getParent().getParent());

        new Thread(
                () -> {
                    try {
                        TimeUnit.SECONDS.sleep(2);
                    } catch (InterruptedException e) {
                        //e.printStackTrace();
                    }
                    throw new RuntimeException("run error.");
                }
                , "test-thread").start();
    }

    //某个异常的默认异常处理器
    public static void testUncaughtExceptionHandler() {

        Thread thread = new Thread(() -> {
            System.out.println("111");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            throw new RuntimeException("you wenti ");

        });

        thread.setUncaughtExceptionHandler(
                (t, e) -> {

                    System.out.println("xxxxxx");
                    e.printStackTrace();
                    //有问题重新连接
//                try {
//                    testUncaughtExceptionHandler();
//                } catch (Exception e1) {
//                    e1.printStackTrace();
//                }
                });

        thread.start();
    }


    //线程设定thread.setUncaughtExceptionHandler则只执行这个
    //若未设定，则使用Thread.setDefaultUncaughtExceptionHandler以及线程组的uncaughtException
    public static void testAllHandleException() {

        Thread.setDefaultUncaughtExceptionHandler(
                (t, e) -> System.out.println("xxxxxx,message:" + e.getMessage()));

        ThreadGroupTest.MyThreadGroup myThreadGroup = new ThreadGroupTest.MyThreadGroup("myThreadGroup");

        Thread thread = new Thread(myThreadGroup,
                () -> {
                    System.out.println("111");
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    throw new RuntimeException("you wenti ");

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
}
