package com.wolf.concurrenttest.underjvm.classfile;

/**
 * Description: 为测试class结构
 * javac com/wolf/concurrenttest/underjvm/classfile/TestClass.java
 * javap com/wolf/concurrenttest/underjvm/classfile/TestClass
 * Created on 2021/7/23 6:22 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class TestClass {
    private int m;

    public int inc() {
        return m + 1;
    }
}
