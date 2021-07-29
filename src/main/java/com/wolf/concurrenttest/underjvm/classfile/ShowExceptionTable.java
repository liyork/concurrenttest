package com.wolf.concurrenttest.underjvm.classfile;

/**
 * Description: 演示在字节码层面try-catch-finally如何体现
 * Created on 2021/7/24 8:19 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ShowExceptionTable {
    public int inc() {
        int x;
        try {
            x = 1;
            return x;
        } catch (Exception e) {
            x = 2;
            return x;
        } finally {
            x = 3;
        }
    }
}
