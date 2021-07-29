package com.wolf.concurrenttest.underjvm.classload;

/**
 * Description: 测试构造器和<init>，这俩的关系是?从字节码看，似乎<init>中的内容，已经能被构造器给包了?
 * javac com/wolf/concurrenttest/underjvm/classload/InvokeConstructorTest.java
 * javap -v com/wolf/concurrenttest/underjvm/classload/InvokeConstructorTest
 * Created on 2021/7/28 10:14 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class InvokeConstructorTest {
    int aaaaa = 111111;

    {
        System.out.println("instance block");
    }

    public InvokeConstructorTest(String msg) {
        System.out.println("a:" + aaaaa);
        System.out.println("msg:" + msg);
    }

    public void test() {
        System.out.println("test");
    }

    public static void main(String[] args) {
        System.out.println("1113333222");
        new InvokeConstructorTest("xxxx").test();
    }
}
