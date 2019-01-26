package com.wolf.concurrenttest.thread;

import java.util.ArrayList;

/**
 * Description:
 * 在arrayList的
 *  public boolean add(E e) {
 * ensureCapacityInternal(size + 1);  // Increments modCount!!  这行设断点
 *
 * point condition : !(Thread.currentThread().getName().equals("main")) && size==9
 * 并选择suspend:thread，理论上来说选择all会在当前线程挂起时整个jvm都挂起，但是不知道为什么切换到线程2时，size变化了！。。
 * 但是选择thread则不会出现这种现象。
 *
 * <br/> Created on 25/03/2018 8:40 PM
 *
 * @author 李超
 * @since 1.0.0
 */
public class DebugSourceCode {

    public static void main(String[] args) {

        ArrayList<Object> list = new ArrayList<Object>();
        for (int i = 0; i < 2; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    for (int j = 0; j < 100000; j++) {
                        list.add(new Object());
                    }
                }
            }, "t" + (i + 1)).start();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, "t3").start();

    }
}
