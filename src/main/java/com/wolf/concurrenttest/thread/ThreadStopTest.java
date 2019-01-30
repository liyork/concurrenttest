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
public class ThreadStopTest {

    private static final Logger logger = LoggerFactory.getLogger(ThreadStopTest.class);

    public static void main(String[] args) {
//        testErrorWayToStopThread();

//        testRightWayToStopThreadUseIsInterrupted();
//        testRightWayToStopThreadUseVolatile();
        testRightWayToStopSuggest();

//        testListVisibility();
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
    private static void testRightWayToStopThreadUseIsInterrupted() {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                System.out.println("====>111");
                for (int x = 0; x < 99999999; x++) {
                    if (!Thread.currentThread().isInterrupted()) {
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
        };
        Thread thread = new Thread(runnable);
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
    private static void testRightWayToStopThreadUseVolatile() {

        TestStopRunnable testStopRunnable = new TestStopRunnable();
        Thread thread = new Thread(testStopRunnable);
        thread.start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("stop the thread..");
        testStopRunnable.setNeedRun(false);
    }

    //不论是list.size()或list == null判断，都是两个线程可见性问题，都需要加volatile，否则程序不退出
    private static void testListVisibility() {

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

    //推荐使用volatile + isInterrupted+catch后再interupt进行处理。
    //既能用isInterrupted停止循环，又能处理wait、sleep等待时及时响应中断
    private static void testRightWayToStopSuggest() {

        TestStopRunnable3 testStopRunnable = new TestStopRunnable3();
        Thread thread = new Thread(testStopRunnable);
        thread.start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        testStopRunnable.stop();
    }

    public static class TestStopRunnable3 implements Runnable {

        private volatile boolean closed;
        private Thread curThread;

        @Override
        public void run() {
            curThread = Thread.currentThread();
            while (!closed && !Thread.currentThread().isInterrupted()) {
                System.out.println(Thread.currentThread().getName() + " is running...");
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    logger.error("TestStopRunnable3 run error ", e);
                    Thread.currentThread().interrupt();//没有立即终止，而是设定标志位，让后面方法继续后再通过while判断退出，保留整体一致性。todo 这里也要思考是该如何保证一致性？
                }
            }
            System.out.println("after run method invoke..");
        }

        public void stop() {
            closed = true;
            System.out.println("curThread.getName():" + curThread.getName());
            curThread.interrupt();
        }
    }

    //多线程间通信，需要添加volatile或者同步快保证线程之间的可见性。
    static class TestStopRunnable implements Runnable {

        //使用volatile保持多线程间的可见性
        private volatile boolean isNeedRun = true;

        public void setNeedRun(boolean isNeedRun) {
            this.isNeedRun = isNeedRun;
        }

        @Override
        public void run() {
            System.out.println("====>111");
            while (isNeedRun) {
//                synchronized (lock) {//进入synchronized的线程可以看到由同一个锁保护之前的所有修改
                System.out.println("is running...");
            }
            System.out.println("====>222");
        }
    }

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
            while (list.size() != 1) {
//        while(list == null) {
            }
            System.out.println("====>222");
        }
    }

    ;


}
