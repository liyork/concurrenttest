package com.wolf.concurrenttest.underjvm.classload;

/**
 * Description: 方法静态解析演示
 * 静态方法sayHello只可能属于类型StaticResolution，没有任何途径可以覆盖或隐藏这个方法
 * javac com/wolf/concurrenttest/underjvm/classload/StaticResolution.java
 * javap -v com/wolf/concurrenttest/underjvm/classload/StaticResolution
 * 可看到使用invokestatic命令调用sayHello()方法，而且其调用的方法版本已经在编译期就明确以常量池项形式固化在字节码指令的参数中
 * Created on 2021/7/27 9:08 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class StaticResolution {
    public static void sayHello() {
        System.out.println("hello world");
    }

    public static void main(String[] args) {
        StaticResolution.sayHello();
    }
}
