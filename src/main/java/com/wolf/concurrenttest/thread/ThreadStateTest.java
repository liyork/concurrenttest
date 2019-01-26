package com.wolf.concurrenttest.thread;

/**
 * Description:
 * <p>
 * <br/> Created on 3/3/18 8:19 PM
 *
 * @author 李超
 * @since 1.0.0
 */
public class ThreadStateTest {

    public static void main(String[] args) throws InterruptedException {
//        testBaseState();
//        testTimeWait();
//        testBlock();
        testWaiting();
    }

    /**
     * new后未执行start，都是new状态
     * 执行start后，是RUNNABLE
     * 执行完，是TERMINATED
     *
     * @throws InterruptedException
     */
    private static void testBaseState() throws InterruptedException {
        Runnable target = new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + " ,myThread state:" + Thread.currentThread().getState());
            }
        };

        MyThread myThread = new MyThread(target);
        System.out.println(Thread.currentThread().getName() + " ,myThread state:" + myThread.getState());
        myThread.start();
        Thread.sleep(1000);
        System.out.println(Thread.currentThread().getName() + " ,myThread state:" + myThread.getState());
    }

    //sleep(time)/wait(time)/join(time)/
    private static void testTimeWait() throws InterruptedException {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("before sleep");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("after sleep");
            }
        });

        thread.start();
        Thread.sleep(1000);
        System.out.println(Thread.currentThread().getName() + " ,thread state:" + thread.getState());
    }


    private static void testBlock() throws InterruptedException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    test1();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    test1();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        Thread.sleep(1000);
        System.out.println(thread.getName() + "  state:" + thread.getState());
    }

    //wait/join
    private static void testWaiting() throws InterruptedException {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    test2();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        Thread.sleep(1000);
        System.out.println(thread.getName() + "  state:" + thread.getState());
    }


    static class MyThread extends Thread {
        public MyThread(Runnable target) {
            super(target);
            System.out.println(Thread.currentThread().getName() + " ,myThread state:" + this.getState());
        }
    }

    private static synchronized void test1() throws InterruptedException {
        System.out.println(Thread.currentThread().getName() + " ,run test" );
        Thread.sleep(5000);
    }

    private static synchronized void test2() throws InterruptedException {
        System.out.println(Thread.currentThread().getName() + " ,run test" );
        ThreadStateTest.class.wait();
    }
}
