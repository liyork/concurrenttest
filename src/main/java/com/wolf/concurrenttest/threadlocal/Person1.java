package com.wolf.concurrenttest.threadlocal;

/**
 * <p> Description:
 *
 * Java.lang.IllegalMonitorStateException
 * 违法的监控状态异常。当某个线程试图等待一个自己并不拥有的对象（O）的监控器或者通知其他线程等待该对象（O）的监控器时，抛出该异常。
 * <p/>
 * Date: 2016/6/23
 * Time: 11:50
 *
 * @author 李超
 * @version 1.0
 * @since 1.0
 */
class Person1 {

    private Object lock = new Object();

    //我们自己构造ThreadLocal是通过匿名内部类构造的。
    //lambda中，构造了SuppliedThreadLocal是ThreadLocal的子类，我们函数是Supplier,执行get时就会调用SuppliedThreadLocal的get
    //进而调用()->{}的get方法。提供了便捷性
    private static ThreadLocal<TestNode> threadLocal = ThreadLocal.withInitial(() -> {
        TestNode testNode = new TestNode();
        testNode.name = "x1";
        return testNode;
    });

    public void getAndSet() {
        //头一开始，synchronized放在了方法上，jvm内部使用他自己的锁，然后我又用lock.wait所以就抛异常了IllegalMonitorStateException..
        synchronized(lock) {
            System.out.println(Thread.currentThread().getName() + " get and set");
            TestNode testNode = threadLocal.get();
            System.out.println(Thread.currentThread().getName() + " " + testNode.name);
            testNode.name = "y1";
            //修改属性后不用再设定回去，因为线程会用对象的引用
            //threadLocal.set(testNode);
            try {
                System.out.println(Thread.currentThread().getName() + " begin wait");
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + " finish wait");

            TestNode testNode2 = threadLocal.get();
            System.out.println(Thread.currentThread().getName() + " " + testNode2.name);
        }
    }

    public void getAndSet2() {
        synchronized(lock) {
            System.out.println(Thread.currentThread().getName() + " get and set 2");
            try {
                System.out.println(Thread.currentThread().getName() + " begin sleep");
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + " finish sleep");
            System.out.println(Thread.currentThread().getName() + " notify");
            lock.notifyAll();
        }
    }

    static class TestNode {
        String name;
    }
}