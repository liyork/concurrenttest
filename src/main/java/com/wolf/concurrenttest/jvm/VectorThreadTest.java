package com.wolf.concurrenttest.jvm;

import java.util.Vector;

/**
 * Description:vector满足hanpenbefore，但是i不满足，导致使用vector时，没有绝对的线程安全
 * <br/> Created on 11/11/17 8:52 AM
 *
 * @author 李超
 * @since 1.0.0
 */
public class VectorThreadTest {

    public static Vector<String> vector = new Vector<>();

    public static void main(String[] args) throws InterruptedException {

        int i =0;
        while (i <20) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 10; i++) {
                        vector.remove(i);
                    }
                }
            }, "remove thread").start();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 10; i++) {
                        vector.get(i);
                    }
                }
            }, "get thread").start();

        }
    }
}
