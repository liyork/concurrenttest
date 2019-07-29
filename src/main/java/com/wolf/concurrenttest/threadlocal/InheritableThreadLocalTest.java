package com.wolf.concurrenttest.threadlocal;

import org.junit.Test;

/**
 * Description:
 * 解决当前线程创建新线程但是threadlocal想继续传递的问题
 * <p>
 * 调用new Thread的start方法是当前线程执行的，所以里面的this就是当前线程调用的thread的start方法
 * 这时jvm底层会启动一个线程执行thread对象的run方法，this还是thread，不过线程变了，然后run方法调用runnalble的run方法。
 * <p>
 * new Thread()时，会按照当前线程的inheritableThreadLocals创建一个新map放入新线程的inheritableThreadLocals。
 * <br/> Created on 2016/8/10 8:30
 *
 * @author 李超()
 * @since 1.0.0
 */
public class InheritableThreadLocalTest {

    //静态对象只有一个！！！
//    private static ThreadLocal<String> local = new ThreadLocal<String>() {
    private static ThreadLocal<String> local = new InheritableThreadLocal<String>() {
        //只有第一次直接get时会触发，如果先设定了再get就永远不会触发了
        @Override
        protected String initialValue() {
            System.out.println("initialValue this:" + this.getClass());
            System.out.println(Thread.currentThread().getName() + ":initial");
            return "xxxx";
        }

        //构造新线程时会触发这个设定子线程的值
        @Override
        protected String childValue(String parentValue) {
            return parentValue + ":zzz";
        }
    };

    public static void main(String[] args) throws InterruptedException {

        new InheritableThreadLocalTest().testSubThread();
    }


    private void testSubThread() throws InterruptedException {
        System.out.println("testSubThread this:" + this.getClass());//应用创建的对象
        String s1 = InheritableThreadLocalTest.local.get();//初始化时设定inheritableThreadLocals
        System.out.println("parent first get===>" + s1);


        //这里起初以为新启动线程的threadlocal会随着启动者的当时的状况而定，
        // 后来通过打印当前线程，ThreadLocal不具有继承性,InheritableThreadLocal具有
//    try {
//       Thread.sleep(2000);
//    } catch (InterruptedException e) {
//       e.printStackTrace();
//    }

        InheritableThreadLocalTest.local.set("qqq");
        String s = InheritableThreadLocalTest.local.get();
        System.out.println("parent second get===>" + s);

        Runnable runnable = new Runnable() {//构造时，使用当前线程inheritableThreadLocals
            @Override
            public void run() {
                System.out.println("run this:" + this.getClass());//InheritableThreadLocalTest$2@4f085a0a,是java底层线程又构造了一个InheritableThreadLocalTest类文件中的InheritableThreadLocal内部类(其实就是Runnable)调用此方法
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String s1 = InheritableThreadLocalTest.local.get();
                System.out.println("sub===>" + s1);
                InheritableThreadLocalTest.local.set("qqq1");//main线程和thread-0线程是两个线程，也就内部有两个map，两者互不干涉
                System.out.println("sub2===>" + InheritableThreadLocalTest.local.get());
            }
        };

        MyThread thread = new MyThread(runnable);
        thread.start();//this:Thread[Thread-0,5,main]

        thread.join();

        System.out.println("parent second get2===>" + InheritableThreadLocalTest.local.get());

        InheritableThreadLocalTest.local.set("qqq1");
        System.out.println("parent second get3===>" + InheritableThreadLocalTest.local.get());

    }

    //this标识当前方法所在对象。匿名内部类中的方法调用this同样隶属于匿名内部类对象
    @Test
    public void testAnonymousInnerClassName() {
        new TestThis() {
            @Override
            public void initial() {
                super.initial();
                System.out.println("initial this:" + this.getClass());//匿名内部类InheritableThreadLocalTest$3@71dac704
            }
        }.get();

        new TestThis().get();//com.wolf.concurrenttest.threadlocal.TestThis
    }

}
