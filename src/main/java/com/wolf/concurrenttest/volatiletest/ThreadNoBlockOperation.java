package com.wolf.concurrenttest.volatiletest;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Description:
 * <br/> Created on 2017/6/23 22:15
 *
 * @author 李超
 * @since 1.0.0
 */
public class ThreadNoBlockOperation {

    private static HashMap<String, AtomicInteger> map = new HashMap<>();
    private static volatile AtomicInteger atomicInteger = new AtomicInteger(0);

    //如果加上了volatile，不论赋值还是添加新元素都能保证里面元素也都有volatile语意。不知道为何？。。。
    private static volatile Integer[] tabs = new Integer[10];

    public static void main(String[] args) {
//        testVolatileNotToArrayContent();
        testVolatileNotToArrayContent2();
//        simpleTest();
//        testCoordinate();
    }

    /**
     * 模拟一个线程1毫秒1放入数据，一个线程10秒一获取数据，
     * 不使用concurrenthashmap和单纯加锁，可以适当允许少数不一致，高吞吐
     *
     *
     * increase key:xx0 ,7503
     * increase key:xx0 ,7504
     * increase key:xx0 ,7505
     * increase key:xx0 ,7506
     * increase key:xx0 ,7507
     * increase key:xx0 ,7508 //一直涨...
     * xx0__7508 //获取到的数值与上面一样，代表可见性
     * nextValue:7508
     * sleep:2 //休息，让另一个+，这边可以cas设值
     * increase key:xx0 ,7509
     * diffValue:0
     * finishSet:false //没有设进去，重新试
     *
     * nextValue:7509 //重新试新的开始
     * sleep:9
     * increase key:xx0 ,7510
     * increase key:xx0 ,7511
     * increase key:xx0 ,7512
     * increase key:xx0 ,7513
     * increase key:xx0 ,7514
     * increase key:xx0 ,7515
     * increase key:xx0 ,7516
     * increase key:xx0 ,7517
     * increase key:xx0 ,7518
     * diffValue:1
     * finishSet:false
     *
     * nextValue:7518
     * sleep:3
     * increase key:xx0 ,7519
     * diffValue:10
     * finishSet:false
     *
     * nextValue:7519
     * sleep:3
     * increase key:xx0 ,7520
     * increase key:xx0 ,7521
     * increase key:xx0 ,7522
     * diffValue:11
     * finishSet:false
     *
     * nextValue:7522
     * sleep:6
     * increase key:xx0 ,7523
     * increase key:xx0 ,7524
     * increase key:xx0 ,7525
     * increase key:xx0 ,7526
     * increase key:xx0 ,7527
     * diffValue:14
     * finishSet:false
     *
     * nextValue:7528
     * sleep:9
     * increase key:xx0 ,7528
     * increase key:xx0 ,7529
     * diffValue:20
     * finishSet:false
     *
     * nextValue:7529
     * sleep:4
     * increase key:xx0 ,7530
     * increase key:xx0 ,7531
     * increase key:xx0 ,7532
     * diffValue:21
     * finishSet:false
     *
     * nextValue:7532
     * sleep:2
     * diffValue:24
     * finishSet:true //设定成功，另一个线程重新开始算,从24开始
     * increase key:xx0 ,25
     * increase key:xx0 ,26
     * increase key:xx0 ,27
     * increase key:xx0 ,28
     * increase key:xx0 ,29
     */
    private static void testCoordinate() {
        //取数据
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    //每10秒执行一次
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Iterator<String> iterator = map.keySet().iterator();
                    while(iterator.hasNext()) {
                        String key = iterator.next();
                        AtomicInteger atomicInteger = map.get(key);
                        int currentValue = atomicInteger.get();
                        //使用数据，记录频率
                        System.out.println(key + "__" + currentValue);

                        Random random = new Random();
                        //重新赋值：设定0或者nextValue - currentValue
                        boolean finishSet = false;
                        while(!finishSet) {
                            //System.out.println("currentValue:" + currentValue);
                            //atomicInteger中的value是volatile
                            int nextValue = atomicInteger.get();
                            System.out.println("nextValue:" + nextValue);
                            try {
                                int i = random.nextInt(10);
                                System.out.println("sleep:" + i);//机器是多核的所以得特殊模拟下。
                                Thread.sleep(i);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            int diffValue = nextValue - currentValue;
                            System.out.println("diffValue:" + diffValue);
                            finishSet = atomicInteger.compareAndSet(nextValue, diffValue);
                            System.out.println("finishSet:" + finishSet);
                        }
                    }
                }
            }
        });


        //放数据
        //Random random = new Random();//模拟数据，但是后来想看看对于一个槽的操作即有可能并发冲突，结果如何
        //String[] prefix = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "k"};
        while(true) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String key = "xx" + 0;
            AtomicInteger atomicInteger = map.get(key);
            if(null == atomicInteger) {
                atomicInteger = new AtomicInteger(0);
                map.put(key, atomicInteger);
                System.out.println("new key:" + key + " ,0");
            } else {
                int i1 = atomicInteger.incrementAndGet();
                //map = map;
                System.out.println("increase key:" + key + " ," + i1);
            }
        }
    }

    /**
     * 想测试对于volatile的map中的内容是否也有相同volatile语意
     */
    public static void simpleTest() {
        AtomicInteger a = map.get("a");

        //取数据
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                map.put("a", new AtomicInteger(2));
                AtomicInteger a = map.get("a");
                while(a.get() == 2) {
                    //System.out.println("xx:"+a.get());
                }
                System.out.println("yy:" + a.get());
            }
        });

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        a.incrementAndGet();

        System.out.println("11111111");
        executorService.shutdown();
    }


    /**
     * 根据网上说法，volatile的数组，对里面数据赋值不会被立即可见，但是测试发现似乎能看到。。javap后也没有相关锁，是不是他们用1.6有。。
     */
    public static void testVolatileNotToArrayContent() {
        //取数据
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                while(tabs[0] == null) {

                }
                System.out.println("tabs[0] not null");
            }
        });

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("end ...");
        tabs[0] = new Integer(0);
        executorService.shutdown();
    }

    /**
     * 根据网上说法，volatile的数组，对里面数据修改不会被立即可见，但是测试发现似乎能看到。。javap后也没有相关锁，是不是他们用1.6有。。
     */
    public static void testVolatileNotToArrayContent2() {
        tabs[1] = 1;
        //取数据
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                while(tabs[1] == 1) {

                }
                System.out.println("tabs[1] is 2");
            }
        });

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("end ...");
        tabs[1] = new Integer(2);
        executorService.shutdown();
    }
}
