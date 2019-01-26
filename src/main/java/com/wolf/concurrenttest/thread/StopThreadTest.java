package com.wolf.concurrenttest.thread;

import com.wolf.concurrenttest.common.TakeTimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * <br/> Created on 2017/2/4 11:21
 *
 * @author 李超
 * @since 1.0.0
 */
public class StopThreadTest {

    private static final Logger logger = LoggerFactory.getLogger(StopThreadTest.class);

    public static void main(String[] args) {
//        testErrorWayToStopThread();
//        testRightWayToStopThread1();
//        testRightWayToStopThread2();
//        testRightWayToStopThread3();

        testInterruptBeforeSleep();
//        testRightWayToStopThread4();
    }

    private static void testErrorWayToStopThread() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("====>111");
                TakeTimeUtils.simulateLongTimeOperation(5000000);
                System.out.println("====>222");
            }
        });
        thread.start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("stop the thread..");
        //不建议使用,释放锁会引起不良后果，数据不一致
        thread.stop();
    }

    //可以让非运行时状况的线程(sleep、wait、I/O阻塞)停止
    //注：调用interrupt()的情况是依赖于实际运行的平台的。
    // 在Solaris和Linux平台上将会抛出InterruptedIOException的异常，但是Windows上面不会有这种异常socket中的inputstream和outputstream的read和write方法都不响应中断，可以通过关闭底层socket
    private static void testRightWayToStopThread1() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("====>111");
                for(int x = 0; x < 99999999; x++) {
                    if(!Thread.currentThread().isInterrupted()) {
                        x++;
                        int y = x + 1;
                        int z = y * 333;
                        //这里如果还有wait,sleep,join还需要try,抛出异常则catch中的isInterrupted=false
                        int c = z + 333 - 999 * 333 / 4444;
                        int i = c * 45559 * 2232 - 22;
                        System.out.println(i);
                    }
                }
                System.out.println("====>222");
            }
        });
        thread.start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("stop the thread..");
        thread.interrupt();
    }

    //这个应该比较推荐,但是如果遇到阻塞方法则检查不到标志位
    private static void testRightWayToStopThread2() {
        TestStopRunnable testStopRunnable = new TestStopRunnable();
        Thread thread = new Thread(testStopRunnable);
        thread.start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("stop the thread..");
//        testStopRunnable.setNeedRun(false);
        testStopRunnable.setA(2);
    }

    //测试list是否在多线程具有可见性。结果：不能，需要使用volatile
    private static void testRightWayToStopThread3() {
        TestStopRunnable2 testStopRunnable = new TestStopRunnable2();
        Thread thread = new Thread(testStopRunnable);
        thread.start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("stop the thread..");
//        testStopRunnable.setList(new ArrayList<>());
        testStopRunnable.add(2);
    }

    //interrupt设定线程被中断了标志，然后线程遇到sleep时抛出异常。不论先sleep还是先interrupt
    private static void testInterruptBeforeSleep() {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                TakeTimeUtils.simulateLongTimeOperation(8000000);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("run finish...");
            }
        });

        thread.start();
        thread.interrupt();
    }

    //推荐使用isInterrupted+catch后再interupt进行处理。
    //既能用isInterrupted停止循环，又能处理wait、sleep等待时及时响应中断
    private static void testRightWayToStopThread4() {
        TestStopRunnable3 testStopRunnable = new TestStopRunnable3();
        Thread thread = new Thread(testStopRunnable);
        thread.start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        thread.interrupt();
    }

    public static class TestStopRunnable3 implements Runnable {

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                System.out.println(Thread.currentThread().getName()+" is running...");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    logger.error("TestStopRunnable3 run error ",e);
                    Thread.currentThread().interrupt();//没有立即终止，而是设定标志位，让后面方法继续后再通过while判断退出，保留整体一致性。todo 这里也要思考是该如何保证一致性？
                }
            }
            System.out.println("after run method invoke..");
        }
    };


    //不论直接使用isNeedRun或者a==1都需要添加volatile或者同步快保证线程之间的可见性。
    static class TestStopRunnable implements Runnable {

        //使用volatile保持多线程间的可见性
//    private volatile boolean isNeedRun = true;
        private boolean isNeedRun = true;
        private volatile int a = 1;
        private Object lock = new Object();

        public void setNeedRun(boolean isNeedRun) {
            this.isNeedRun = isNeedRun;
        }

        public void setA(int a ) {
            this.a = a;
        }



        @Override
        public void run() {
            System.out.println("====>111");
            while(isNeedRun) {
//        while(a !=2) {
                synchronized (lock) {//进入synchronized的线程可以看到由同一个锁保护之前的所有修改

                }
            }
            System.out.println("====>222");
        }
    };


    //不论是list.size()或list == null判断，都是两个线程可见性问题，都需要加volatile
    static class TestStopRunnable2 implements Runnable {

        private volatile List<Integer> list = new ArrayList<>();
//    private volatile List<Integer> list ;

        public void add(Integer value) {
            list.add(value);
        }

        public void setList(List<Integer> list) {
            this.list = list;
        }

        @Override
        public void run() {
            System.out.println("====>111");
            while(list.size() != 1) {
//        while(list == null) {
            }
            System.out.println("====>222");
        }
    };


}
