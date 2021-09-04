package com.wolf.concurrenttest.bfbczm.foundation2;

/**
 * Description: 线程安全保障方式
 * Created on 2021/9/3 7:00 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ThreadNotSafeInteger {
    private int value;
    private int value1;
    private volatile int value2;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public synchronized int getValue1() {
        return value1;
    }

    public synchronized void setValue1(int value1) {
        this.value1 = value1;
    }

    public int getValue2() {
        return value2;
    }

    public void setValue2(int value2) {
        this.value2 = value2;
    }
}
