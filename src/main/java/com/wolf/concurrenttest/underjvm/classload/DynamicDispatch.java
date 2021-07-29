package com.wolf.concurrenttest.underjvm.classload;

/**
 * Description: 方法动态分派演示，通过栈顶对象的实际类型
 * javac com/wolf/concurrenttest/underjvm/classload/DynamicDispatch.java
 * javap -v com/wolf/concurrenttest/underjvm/classload/DynamicDispatch
 * Created on 2021/7/27 12:49 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class DynamicDispatch {
    static abstract class Human {
        protected abstract void sayHello();
    }

    static class Man extends Human {
        @Override
        protected void sayHello() {
            System.out.println("man say hello");
        }
    }

    static class Woman extends Human {
        @Override
        protected void sayHello() {
            System.out.println("woman say hello");
        }
    }

    // 原因是因为两个变量的实际类型不同，
    public static void main(String[] args) {
        Human man = new Man();
        Human woman = new Woman();
        man.sayHello();
        woman.sayHello();
        man = new Woman();
        man.sayHello();
    }
}
