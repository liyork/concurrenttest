package com.wolf.concurrenttest.volatiletest;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * <br/> Created on 2017/2/23 8:33
 *
 * @author 李超
 * @since 1.0.0
 */
class VolatileTest {

    private volatile int a;

    private static volatile Map<String, String> map = new HashMap<>();

    public static void main(String[] args) throws InterruptedException {
//        volatileTest.test2();
//        VolatileTest.test3();
        VolatileTest.test4();
    }

    /**
     * 想模拟下，先获取，然后有人设置，再获取下看看能不能获取到，但是一直打印东西无法模拟出来的。。。
     */
    private void test() {
        final VolatileTest volatileTest = new VolatileTest();

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                //先获取一遍，得到0
                volatileTest.get();
                System.out.println(Thread.currentThread().getName() + " to wait...");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //由于之前下面设定了值，所以再获取的时候由于volatile保证，可以读到
                System.out.println(volatileTest.get());
            }
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + " set...");
                volatileTest.set(2);
            }
        });

        executorService.shutdown();
    }

    /**
     * 不加volatile则一直循环，由于线程只从自己内存中取数据
     */
    private void test2() {
        final VolatileTest volatileTest = new VolatileTest();

        ExecutorService executorService = Executors.newFixedThreadPool(2);


        executorService.execute(new Runnable() {
            @Override
            public void run() {
                while(volatileTest.get() != 2) {
                    //不能打印，已打印就好了，可能就得模拟这种快形式，让thread来不及反映
//                    System.out.println(Thread.currentThread().getName() + " to wait...");
                }
            }
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        volatileTest.set(2);

        executorService.shutdown();
    }


    private static void test3() {

        final VolatileTest volatileTest = new VolatileTest();
        ExecutorService executorService = Executors.newCachedThreadPool();

        for(int i = 0; i < 20000; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    volatileTest.increase();
                }
            });
        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(volatileTest.a);

        executorService.shutdown();
    }

    //对于直接覆盖的map，不会影响到线程中已经使用的map。这可以用于定时加载数据然后一次性直接覆盖。
    private static void test4() throws InterruptedException {

        map.put("a", "b");

        new Thread(()->{
            int size = map.size();
            System.out.println(Thread.currentThread().getName() + " size:" + size);

            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println(Thread.currentThread().getName() + " size:" + size);
        }).start();

        TimeUnit.SECONDS.sleep(1);
        map = new HashMap<>();
        System.out.println("111111");
    }

    //volatile变量只能保证多个线程之间的操作可见性，但是不能保证多个操作的原子性
    private void increase(){
        a = a +1;//与a++一样，都不能保证原子执行
    }


    public void set(int l) {
        a = l;
    }

    public int get() {
        return a;
    }
}
