package com.wolf.concurrenttest.thread;

import com.wolf.concurrenttest.common.TakeTimeUtils;
import org.junit.Test;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * <p> Description:
 * 并不是用来管理thread，而是针对thread的一个组织。
 * <p>
 * 线程组既包含线程又包含线程组。
 * <p>
 * <p/>
 * Date: 2015/9/14
 * Time: 9:43
 *
 * @author 李超
 * @version 1.0
 * @since 1.0
 */
public class ThreadGroupTest {

    public static void main(String[] args) {
        testInterruptGroup();
    }

    //默认线程组就是创建此线程的那个线程所在组，main是jvm启动第一个创建的线程。
    @Test
    public void testBase() {

        Thread thread = new Thread("defaultGroupThread");
        System.out.println("main and defaultGroupThread is one threadGroup:" + (thread.getThreadGroup() == Thread.currentThread().getThreadGroup()));
        System.out.println("default thread group name:" + thread.getThreadGroup().getName());

        ThreadGroup testThreadGroup = new ThreadGroup("testThreadGroup");
        Thread assignGroupThread = new Thread(testThreadGroup, "assignGroupThread");
        System.out.println("assignGroupThread group name" + assignGroupThread.getThreadGroup().getName());
        System.out.println("assignGroupThread group:" + assignGroupThread);
        System.out.println("is one assign threadGroup:" + (assignGroupThread.getThreadGroup() == testThreadGroup));
    }

    //查看组内线程，enumerate是当时一个快照
    @Test
    public void testEnumerateThread() {

        ThreadGroup threadGroup = new ThreadGroup("myGroup");
        Thread thread = new Thread(threadGroup,
                () -> {
                    try {
                        TimeUnit.SECONDS.sleep(3);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }, "t1");
        thread.start();

        new Thread(//使用默认线程组，即当前线程(main)的线程组
                () -> {
                    try {
                        TimeUnit.SECONDS.sleep(3);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }, "t2").start();

        ThreadGroup mainGroup = Thread.currentThread().getThreadGroup();

        Thread[] threads = new Thread[mainGroup.activeCount()];
        int enumerateRecurse = mainGroup.enumerate(threads);
        System.out.println("enumerateRecurse:" + enumerateRecurse);

        for (int i = 0; i < mainGroup.activeCount(); i++) {
            System.out.printf("Thread %s: %s\n", threads[i].getName(), threads[i].getState());
        }

        int enumerateNonRecurse = mainGroup.enumerate(threads, false);
        System.out.println("enumerateNonRecurse:" + enumerateNonRecurse);
    }

    //查看线程组
    @Test
    public void testEnumerateThreadGroup() throws InterruptedException {

        ThreadGroup threadGroup1 = new ThreadGroup("myGroup1");
        new ThreadGroup(threadGroup1, "myGroup2");

        TimeUnit.MILLISECONDS.sleep(2);

        ThreadGroup mainGroup = Thread.currentThread().getThreadGroup();

        ThreadGroup[] threadGroups = new ThreadGroup[mainGroup.activeGroupCount()];

        int enumerateRecurse = mainGroup.enumerate(threadGroups);
        System.out.println("enumerateRecurse:" + enumerateRecurse);

        for (int i = 0; i < mainGroup.activeGroupCount(); i++) {
            System.out.printf("threadGroup %s: %s\n", threadGroups[i].getName(), threadGroups[i].getMaxPriority());
        }

        int enumerateNonRecurse = mainGroup.enumerate(threadGroups, false);
        System.out.println("enumerateNonRecurse:" + enumerateNonRecurse);
    }

    @Test
    public void testThreadGroupMethod() throws InterruptedException {

        //创建5个线程，并入group里面进行管理
        ThreadGroup threadGroup = new ThreadGroup("Searcher");
        SearchTask searchTask = new SearchTask();
        for (int i = 0; i < 5; i++) {
            Thread thread = new Thread(threadGroup, searchTask);
            thread.start();//线程只有运行才有组的概念，注释掉则都是0
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Thread.sleep(500);

        System.out.println();
        //通过这种方法可以看group里面的所有信息
        System.out.printf("activeGroupCount: %d\n", threadGroup.activeGroupCount());//子线程组
        System.out.printf("activeCount: %d\n", threadGroup.activeCount());
        System.out.printf("MaxPriority: %d\n", threadGroup.getMaxPriority());
        System.out.printf("Name: %s\n", threadGroup.getName());
        System.out.printf("parant name: %s\n", threadGroup.getParent().getName());
        System.out.println("list start............");
        threadGroup.list();//打印带有层次的
        System.out.println("list end............");

        waitFinish(threadGroup);
    }

    private static void waitFinish(ThreadGroup threadGroup) {
        while (threadGroup.activeCount() > 0) {
            System.out.printf("activeCount: %d\n", threadGroup.activeCount());
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //全部interrupt
    public static void testInterruptGroup() {

        ThreadGroup testGroup = new ThreadGroup("testGroup");
        new Thread(testGroup, () -> {
            while (true) {
                try {
                    TimeUnit.MILLISECONDS.sleep(2);
                } catch (InterruptedException e) {
                    break;
                }
            }

            System.out.println("t1 will exit.");
        }, "t1").start();

        new Thread(testGroup, () -> {
            while (true) {
                try {
                    TimeUnit.MILLISECONDS.sleep(1);
                } catch (InterruptedException e) {
                    break;
                }
            }
            System.out.println("t2 will exit.");
        }, "t2").start();

        testGroup.interrupt();
    }

    @Test
    public void testDestroyGroup() {

        ThreadGroup testGroup = new ThreadGroup("testGroup");

        ThreadGroup mainGroup = Thread.currentThread().getThreadGroup();
        System.out.println("group.isDestroyed:" + testGroup.isDestroyed());

        mainGroup.list();

        testGroup.destroy();

        System.out.println("group.isDestroyed:" + testGroup.isDestroyed());
        mainGroup.list();
    }

    //threadGroup为daemon，不影响线程的daemon，若一个daemon的threadgroup，则里面没有任何active线程时自动destroy
    @Test
    public void testGroupDaemon() throws InterruptedException {

        ThreadGroup testGroup1 = new ThreadGroup("testGroup1");

        new Thread(testGroup1, () -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "group1-thread1").start();

        ThreadGroup testGroup2 = new ThreadGroup("testGroup2");
        new Thread(testGroup2, () -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "group2-thread1").start();

        testGroup2.setDaemon(true);

        TimeUnit.SECONDS.sleep(3);
        System.out.println(testGroup1.isDestroyed());
        System.out.println(testGroup2.isDestroyed());
    }

    //线程组之间有parent概念，线程只有关联的线程组。根ThreadGroup是system
    @Test
    public void testParent() {
        Thread currentThread = Thread.currentThread();
        System.out.println("currentThread:" + currentThread.getName());
        ThreadGroup currentThreadGroup = currentThread.getThreadGroup();
        System.out.println("currentThread().getThreadGroup():" + currentThreadGroup);
        System.out.println("currentThread().getThreadGroup().activeGroupCount:" + currentThreadGroup.activeGroupCount());
        System.out.println("currentThread().getThreadGroup().activeCount:" + currentThreadGroup.activeCount());
        System.out.println("currentThread().getThreadGroup().getParent:" + currentThreadGroup.getParent());
        System.out.println("currentThread().getThreadGroup().getParent.getParent():" + currentThreadGroup.getParent().getParent());

        ThreadGroup threadGroup = new ThreadGroup(currentThreadGroup, "new threadgroup");
        System.out.println("add threadgroup after");
        System.out.println("currentThread().getThreadGroup().activeGroupCount:" + threadGroup.activeGroupCount());
    }

    //若未声明父线程组,则使用当前线程所在线程组作为父。
    @Test
    public void testDefault() {

        ThreadGroup threadGroup2 = new ThreadGroup("new threadgroup2");
        System.out.println("threadGroup2.getParent():" + threadGroup2.getParent());

        Thread currentThread = Thread.currentThread();
        System.out.println("currentThread:" + currentThread.getName());
        ThreadGroup currentThreadGroup = currentThread.getThreadGroup();
        System.out.println("currentThread().getThreadGroup():" + currentThreadGroup);
        System.out.println("currentThread().getThreadGroup().activeGroupCount:" + currentThreadGroup.activeGroupCount());
        System.out.println("currentThread().getThreadGroup().activeCount:" + currentThreadGroup.activeCount());
        System.out.println("currentThread().getThreadGroup().getParent:" + currentThreadGroup.getParent());
        System.out.println("currentThread().getThreadGroup().getParent.getParent():" + currentThreadGroup.getParent().getParent());

        ThreadGroup threadGroup = new ThreadGroup(currentThreadGroup, "new threadgroup");
        System.out.println("add threadgroup after");
        System.out.println("currentThread().getThreadGroup().activeGroupCount:" + threadGroup.activeGroupCount());
    }

    @Test
    public void testTraversal() {
        ThreadGroup mainGroup = Thread.currentThread().getThreadGroup();
        ThreadGroup groupA = new ThreadGroup(mainGroup, "A");
        ThreadGroup groupB = new ThreadGroup(groupA, "B");

        System.out.println("Thread.currentThread().getThreadGroup().activeCount():" + Thread.currentThread().getThreadGroup().activeCount());
        //Returns an estimate of the number of active groups in this thread group and its subgroups.
        System.out.println("Thread.currentThread().getThreadGroup().activeGroupCount():" + Thread.currentThread().getThreadGroup().activeGroupCount());
        ThreadGroup[] listGroup1 = new ThreadGroup[Thread.currentThread().getThreadGroup().activeGroupCount()];
        Thread.currentThread().getThreadGroup().enumerate(listGroup1, true);//递归获取所有子组
        for (ThreadGroup threadGroup : listGroup1) {
            System.out.println(threadGroup.getName());
        }

        ThreadGroup[] listGroup2 = new ThreadGroup[Thread.currentThread().getThreadGroup().activeGroupCount()];
        Thread.currentThread().getThreadGroup().enumerate(listGroup2, false);

        for (ThreadGroup threadGroup : listGroup2) {
            if (null != threadGroup) {
                System.out.println(threadGroup.getName());
            }
        }
    }

    //一个线程错误，组内所有线程都被interrupt
    @Test
    public void testOneCauseAll() throws InterruptedException {

        MyThreadGroup myThreadGroup = new MyThreadGroup("myThreadGroup");
        for (int i = 0; i < 3; i++) {
            Runnable runnable = new MyRunnable(i + "");
            new Thread(myThreadGroup, runnable).start();
        }

        Thread.sleep(2000);
        Thread thread = new Thread(myThreadGroup, new MyRunnable("a"));
        thread.start();

        Thread.sleep(500000);
    }

    private static class MyRunnable implements Runnable {

        String a;

        public MyRunnable(String a) {
            this.a = a;
        }

        @Override
        public void run() {
            //若想让一个线程引起线程组uncaughtException调用，进而影响所有其他线程停止，则不要捕获异常，因为捕获了，就没法触发线程组的uncaughtException方法了
//			try {
            System.out.println(Thread.currentThread() + " isInterrupted:" + Thread.currentThread().isInterrupted());
            int i = Integer.parseInt(String.valueOf(a));
            while (!Thread.currentThread().isInterrupted()) {
                System.out.println("current thread is running:" + Thread.currentThread().getName() + " i:" + i);
                //期初测试使用sleep，那么可能刚好在这一秒遇到interrupt，那么就sleep interrupted。所以下面catch恢复了状态，所以每个runnable中判断不出来了。
//				try {
//					Thread.sleep(1000);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
                TakeTimeUtils.simulateLongTimeOperation(200000);
            }
            System.out.println(Thread.currentThread().getName() + "：finish thread");

//			} catch (Exception e) {
//				e.printStackTrace();
//				System.out.println("xxxxxxxxx");
//			}
        }
    }

    protected static class MyThreadGroup extends ThreadGroup {

        public MyThreadGroup(String name) {
            super(name);
        }

        //由出错误的线程调用dispatchUncaughtException进而调用uncaughtException
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            super.uncaughtException(t, e);
            System.out.println("interrupt in MyThreadGroup.uncaughtException,cause thread:" + t.getName());
            this.interrupt();
        }
    }

    static class SearchTask implements Runnable {

        @Override
        public void run() {
            String name = Thread.currentThread().getName();
            System.out.println("Thread Start ,name:" + name);
            try {
                doTask();
            } catch (InterruptedException e) {
                System.out.printf("Thread %s: Interrupted\n", name);
                return;
            }
            System.out.println("Thread end " + name);
        }

        private void doTask() throws InterruptedException {
            Random random = new Random((new Date()).getTime());
            int value = (int) (random.nextDouble() * 10);
            System.out.printf("Thread %s: sleep:%d\n", Thread.currentThread().getName(), value);
            TimeUnit.SECONDS.sleep(value);
        }
    }
}
