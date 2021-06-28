package com.wolf.concurrenttest.jcip;

/**
 * Description: 测试可见性，没有同步，则jmm可能任意排序，或者每个cpu的各自cache对其他cpu不可见
 * Created on 2021/6/26 4:19 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class NoVisibility {
    private static boolean ready;
    private static int number;

    private static class ReaderThread extends Thread {
        public void run() {
            while (!ready) {
                Thread.yield();
            }
            System.out.println(number);
        }
    }

    public static void main(String[] args) {
        new ReaderThread().start();
        number = 42;
        ready = true;
    }

}
