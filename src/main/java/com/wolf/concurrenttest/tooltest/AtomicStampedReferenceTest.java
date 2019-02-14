package com.wolf.concurrenttest.tooltest;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicMarkableReference;
import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * Description:使用标记解决cas的aba问题
 *
 * 这里就违背了顺序性，所以多个线程并行操作一个变量，就会产生+10再-10的情景。而有stamp与也业务无关的保证cas即保证了顺序性。
 * <br/> Created on 3/6/18 9:42 AM
 *
 * @author 李超
 * @since 1.0.0
 */
public class AtomicStampedReferenceTest {

    public static void main(String[] args) throws InterruptedException {

        //testAtomicStampedReference();

        //testABAProblem();
//        testResolveABA();
        testResolveABA2();

    }

    private static void testAtomicStampedReference() throws InterruptedException {

        AtomicStampedReference<Integer> atomicStampedReference = new AtomicStampedReference<Integer>(1,1);
        for (int i = 1; i < 5; i++) {

            int finalI = i;
            //        int finalI1= 1;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        System.out.println(Thread.currentThread().getName()+" expectedReference:"+finalI+
                                " newReference:"+(finalI + 1) +" expectStamp:"+finalI+ " newStamp:"+(finalI+1));
                        boolean b = atomicStampedReference.compareAndSet(finalI, (finalI + 1) ,
                                finalI, finalI + 1);
                        //finalI1 + "" ,字符串+操作，若未在编译期变成常量，则运行时会构造新的char[0]，那么用==就不对了
                        //boolean b = atomicStampedReference.compareAndSet(finalI1 + "", (finalI1 + 1) + "", finalI1, finalI1 + 1);
                        //System.out.println(b);
                        System.out.println(Thread.currentThread().getName()+" compareAndSet:"+b);
                        if (b) {
                            break;
                        }

                    }
                }
            }).start();
        }


        Thread.sleep(5000);
        System.out.println("finish:"+atomicStampedReference.getReference());

        Thread.sleep(10000);
    }

    //ab线程都在执行cas的加，c线程在ab之间进行了减，本来没有c时，ab只能一个成功，但是若c在中间，那么b看到的是a+后c-的值。
    //下面场景就是满足10元为客户充值一次，但是由于客户消费了，那么很有可能充值多次。
    private static void testABAProblem() throws InterruptedException {

        AtomicInteger money = new AtomicInteger(10);

        //充值1
        new Thread(new Runnable() {
            @Override
            public void run() {
                int monetyTmp = money.get();
                if (monetyTmp == 10) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    boolean result = money.compareAndSet(monetyTmp, monetyTmp + 10);
                    System.out.println(Thread.currentThread().getName()+" produce compareAndSetResult:"+result+" after value:"+money.get());
                }
            }
        }).start();

        //充值2
        new Thread(new Runnable() {
            @Override
            public void run() {
                int monetyTmp = money.get();
                if (monetyTmp == 10) {
                    //免费充值
                    try {
                        Thread.sleep(4000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    boolean result = money.compareAndSet(monetyTmp, monetyTmp + 10);
                    System.out.println(Thread.currentThread().getName()+" produce compareAndSetResult:"+result+" after value:"+money.get());
                }
            }
        }).start();

        //消费
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                int monetyTmp = money.get();
                if (monetyTmp == 20) {
                    boolean result = money.compareAndSet(monetyTmp, monetyTmp - 10);
                    System.out.println(Thread.currentThread().getName()+" consumer compareAndSetResult:"+result+" after value:"+money.get());
                }
            }
        }).start();
    }

    //stamp永远是++的，与业务无关，所以不会回去，那么就不会有aba问题了
    private static void testResolveABA() throws InterruptedException {

        AtomicStampedReference<Integer> money = new AtomicStampedReference<Integer>(10,1);

        //充值1
        new Thread(new Runnable() {
            @Override
            public void run() {
                int stamp = money.getStamp();
                int moneyTmp = money.getReference();
                if (moneyTmp == 10) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    boolean result = money.compareAndSet(moneyTmp, moneyTmp + 10,stamp,stamp+1);
                    System.out.println(Thread.currentThread().getName()+" produce compareAndSetResult:"+result+" after value:"+money.getReference());
                }
            }
        }).start();

        //充值2
        new Thread(new Runnable() {
            @Override
            public void run() {
                //和【充值1】是一个stamp,模拟同一时刻准备进行修改，但是由于其他事情耽误了，但是原始状态还在。
                int stamp = money.getStamp();
                int moneyTmp = money.getReference();
                if (moneyTmp == 10) {
                    try {
                        Thread.sleep(4000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    boolean result = money.compareAndSet(moneyTmp, moneyTmp + 10,stamp,stamp+1);
                    System.out.println(Thread.currentThread().getName()+" produce compareAndSetResult:"+result+" after value:"+money.getReference());
                }
            }
        }).start();

        //消费
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                int moneyTmp = money.getReference();
                int stampTmp = money.getStamp();
                if (moneyTmp == 20) {
                    boolean result = money.compareAndSet(moneyTmp, moneyTmp - 10,stampTmp,stampTmp+1);
                    System.out.println(Thread.currentThread().getName()+" consumer compareAndSetResult:"+result+" after value:"+money.getReference());
                }
            }
        }).start();
    }

    //todo 这个似乎不能解决aba问题！！!
    private static void testResolveABA2() throws InterruptedException {

        AtomicMarkableReference<Integer> money = new AtomicMarkableReference<Integer>(10,false);

        //充值1
        new Thread(new Runnable() {
            @Override
            public void run() {
                int moneyTmp = money.getReference();
                if (moneyTmp == 10) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    boolean result = money.compareAndSet(moneyTmp, moneyTmp + 10,false,true);
                    System.out.println(Thread.currentThread().getName()+" produce compareAndSetResult:"+result+" after value:"+money.getReference());
                }
            }
        }).start();

        //充值2
        new Thread(new Runnable() {
            @Override
            public void run() {
                //和【充值1】是一个stamp,模拟同一时刻准备进行修改，但是由于其他事情耽误了，但是原始状态还在。
                int moneyTmp = money.getReference();
                if (moneyTmp == 10) {
                    try {
                        Thread.sleep(4000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    boolean result = money.compareAndSet(moneyTmp, moneyTmp + 10,false,true);
                    System.out.println(Thread.currentThread().getName()+" produce compareAndSetResult:"+result+" after value:"+money.getReference());
                }
            }
        }).start();

        //消费
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                int moneyTmp = money.getReference();
                if (moneyTmp == 20) {
                    boolean result = money.compareAndSet(moneyTmp, moneyTmp - 10,true,false);
                    System.out.println(Thread.currentThread().getName()+" consumer compareAndSetResult:"+result+" after value:"+money.getReference());
                }
            }
        }).start();
    }
}
