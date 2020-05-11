package com.wolf.concurrenttest.threadlocal;

import org.junit.Test;

import java.lang.reflect.Field;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <p> Description:threadLocal解决变量在不同线程间的隔离性
 * 内部使用原理：
 * threadLocal只是用来操作每个线程的工具，内部有map结构(仅仅是结构)，但是map对象在每个线程内(key是当前threadlocal，value是值)。
 * 关联关系在thread中，可以有多个threadlocal---值。每个threadlocal只关联一个值。
 * <p>
 * threadLocal的get方法，先从当前thread获取其内部的map，然后操作这个map引用。
 * 查找map中的threadLocal使用开方查找而非链表查找
 * <p>
 * 注：每个线程用完threadlocal需要清除掉，不然下个线程有可能是通过线程池获取的，还保留着上次的信息
 * <p>
 * 为什么关系要放在thread内呢？若放在threadlocal中，势必要进行同步操作，因为写读有并发问题。
 * 那么放thread中就没有这种情况。可能就有些浪费资源，多个map
 *
 * WeakReference本身可以作为引用对对象如：
 * Object obj = new Object();
 * WeakReference wr = new WeakReference(obj);
 * obj = null;
 * 这样，在栈内存中有wr引用堆内存的obj，当jvm回收时会查看到wr是弱引用，那么就会对引用的obj清理，然后wr获取时就会null。
 * 而threadlocal中使用的Entry是扩展了WeakReference，多了一个value而已，当ThreadLocalMap.table中的entry的key为
 * null时就是回收了，所以就可以清理entry了。
 * 但是WeakReference如何被清理呢？o,是被threadlocal主动设置为null即没有任何地方引用entry和里面value了。
 * <p>
 * <p/>
 * Date: 2015/9/28
 * Time: 19:39
 *
 * @author 李超
 * @version 1.0
 * @since 1.0
 */
public class ThreadLocalTest {

    //测试每个线程互不影响，另外测试混合threadlocal和count++导致的问题
    @Test
    public void testThreadLocalIsRight() throws InterruptedException {
        final Person person = new Person();
        ExecutorService executorService = Executors.newFixedThreadPool(500);
        final CountDownLatch countDownLatch = new CountDownLatch(500);
        for (int i = 0; i < 500; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    person.increase();
                    System.out.println(Thread.currentThread().getName() + ",age=" + person.getAge());

                    countDownLatch.countDown();
                }
            });
        }

        countDownLatch.await();
        executorService.shutdown();

        System.out.println("person.getCount:" + person.getCount());

        //深刻教训，机器卡死重启，万事要留个后路。。。(停个几秒，或者啥的。。能中断的)
//		while (person.getAge() == 0) {
//			testThreadLocalIsRight(person);
//		}
    }


    /**
     * 对于取膜操作，
     * 想获得0-9，则用任意数%10，可能大神觉得这样费性能
     * 对于&操作，只有都为1的位才能1，所以尽可能的让位中含1则可以达到与取模作用相同但性能要好
     * 这就是为什么threadlocal中，The initial capacity -- MUST be a power of two.
     * 对于7----0111,任意数字&它，范围都会在0000-0111之间，即0-7
     * 2^N – 1 的二进制表示就是 N – 1 个 1
     */
    @Test
    public void testBitManipulation() {
        for (int i = 0; i < 50; i++) {
            System.out.println("i=" + i + " " + (i & (16 - 1)));
        }

        //实现跳跃性随机分布，随着threadlocal被回收，可能随机比顺序分配好
        int base = 0;
        for (int i = 0; i < 50; i++) {
            System.out.println(base & (16 - 1));
            base += 0x61c88647;
        }
    }


    /**
     * 采用“开放定址法”解决冲突的ThreadLocalMap
     * 测试多个threadlocal对于一个线程，内部是如何构造的。使用 hash & (table-1)，不在则新建，如果有冲突则nextIndex。
     */
    @Test
    public void testMultiThreadLocal() {
        ContainMultiTL containMultiTL = new ContainMultiTL();
        containMultiTL.threadLocal1.get();
        containMultiTL.threadLocal2.get();
    }

    @Test
    public void testIfNeedResetWhenUpdateProperty() {
        final Person1 person1 = new Person1();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                person1.getAndSet();
            }
        });

        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                person1.getAndSet2();
            }
        });

        thread.start();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        thread2.start();
    }


    /**
     * 看到TransmittableThreadLocal本想着能不能把thread里面的map弄出来，这样就少了不用holder了，但是自己模拟了一下，似乎不太可行
     * threadLocals在thread中是包结构，即使弄出来，ThreadLocal.ThreadLocalMap也是包结构，还得反射，即使弄出来，Entry[] table也是包结构。。
     *
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public static void testGetInternalMapFromThreadLocal() throws NoSuchFieldException, IllegalAccessException {
        final Person1 person1 = new Person1();
        final Person1 person2 = new Person1();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                person1.getAndSet();
                person2.getAndSet();
            }
        });

        Field threadLocals = Thread.class.getDeclaredField("threadLocals");
        threadLocals.setAccessible(true);
//        Object o = (ThreadLocal.ThreadLocalMap)threadLocals.get(thread);
//        System.out.println(o);

    }

}