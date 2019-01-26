package com.wolf.concurrenttest.thread;

import java.util.ArrayList;

/**
 * Description:
 * <br/> Created on 2017/4/26 13:02
 *
 * @author 李超
 * @since 1.0.0
 */
public class ArrayListMultiThreadTest {

    //线程不安全
    static ArrayList<Integer> a1 = new ArrayList<>(10);
    //线程安全
//    static Vector<Integer> a1 = new Vector<>(10);
    public static class AddThread implements Runnable {

        @Override
        public void run() {
            for (int i = 0; i < 1000000; i++) {
                try {
                    a1.add(i);
                } catch (Exception e) {
                    System.out.println(Thread.currentThread().getName());
                    e.printStackTrace();
                    System.out.println("size:"+a1.size());
                    System.exit(0);
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(new AddThread());
        Thread t2 = new Thread(new AddThread());
        t1.start();
        t2.start();

        t1.join();
        t2.join();
        System.out.println("final .."+a1.size());
    }

    //=======错误1
//    Thread-0
//    size:42268
//    java.lang.ArrayIndexOutOfBoundsException: 4164
//    at java.util.ArrayList.add(ArrayList.java:412)
//    at com.wolf.test.thread.ArrayListMultiThreadTest$AddThread.run(ArrayListMultiThreadTest.java:21)
//    at java.lang.Thread.run(Thread.java:722)
    //问题原因：出问题的线程当时的size到达了42268，而出问题的ArrayIndexOutOfBoundsException下标才4164，
    //那应该是那时出问题的线程的elementData比4164小,线程之间不定时什么顺序执行后导致那时刻的数组过小


    //=======错误2
    //final ..1292430
    //问题原因：设定了重复数据


}
