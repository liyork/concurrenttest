package com.wolf.concurrenttest.thread;

import java.util.Random;

/**
 * Description:32位jvm上long和double是非原子的读、写(32位机器每次只能操作32位，64位需要两次操作，所以有线程问题)，可以使用volatile
 * 64位的jvm没有这个问题
 * <br/> Created on 2018/3/2 15:21
 *
 * @author 李超
 * @since 1.0.0
 */
public class UnatomicLongTest implements Runnable {

    private static long test = 0;

    private final long val;

    public UnatomicLongTest(long val) {
        this.val = val;
    }

    @Override
    public void run() {
        Random random = new Random();
        while (!Thread.interrupted()) {
            test = val;//两个线程同时断写test变量，如果test变量的读写操作是原子性的，那么test只能是-1或者0
            try {//添加可能线程切换，防止只处理当前cpu的缓存未刷新主内存
                Thread.sleep(random.nextInt(100));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Thread t1 = new Thread(new UnatomicLongTest(-1));
        Thread t2 = new Thread(new UnatomicLongTest(0));

        System.out.println("-1-->" + Long.toBinaryString(-1));
        System.out.println("0-->" + Long.toBinaryString(0));
        System.out.println("0(64)-->" + pad(Long.toBinaryString(0), 64));

        t1.start();
        t2.start();

        //如果test、switchVal的操作是原子性的,那么就应该是死循环，否则就会跳出该循环
        long switchVal;
        while ((switchVal = test) == -1 || switchVal == 0) {
            //System.out.println("testing...");
            Random random = new Random();
            try {
                Thread.sleep(random.nextInt(100));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("switchVal(64)-->" + pad(Long.toBinaryString(switchVal), 64));
        System.out.println(switchVal);

        t1.interrupt();
        t2.interrupt();
    }

    //将0补齐到固定长度
    private static String pad(String s, int targetLength) {
        int n = targetLength - s.length();
        for (int x = 0; x < n; x++) {
            s = "0" + s;
        }
        return s;
    }

}
