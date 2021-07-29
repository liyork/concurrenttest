package com.wolf.concurrenttest.underjvm.classload;

import java.util.Random;

/**
 * Description: 方法静态分派演示
 * javac com/wolf/concurrenttest/underjvm/classload/StaticDispatch.java
 * javap -v com/wolf/concurrenttest/underjvm/classload/StaticDispatch
 * 所有依赖静态类型来决定方法执行版本的分派动作，称为静态分派。典型应用时方法重载。
 * 静态分派发生在编译阶段，因此确定静态分派的动作是实际上不是由虚拟机执行。
 * Created on 2021/7/27 9:26 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class StaticDispatch {
    static abstract class Human {

    }

    static class Man extends Human {

    }

    static class Woman extends Human {

    }

    public void sayHello(Human guy) {
        System.out.println("hello,guy!");
    }

    public void sayHello(Man guy) {
        System.out.println("hello,gentleman!");
    }

    public void sayHello(Woman guy) {
        System.out.println("hello,lady!");
    }

    public static void main(String[] args) {
        //test();

        explain();
    }

    // Human为静态类型(Static Type)，Man为实际类型(Actual Type)
    // 两者都可能会发生变化，区别是静态类型的变化仅仅在使用时发生，变量本身的静态类型不会被改变，且最终的静态类型是在编译期可知的
    // 实际类型变化的结果在运行期才可确定，编译器在编译程序时并不知道一个对象的实际类型是什么。
    private static void explain() {
        Human man = new Man();

        // 实际类型变化，编译期不可知
        Human human = (new Random()).nextBoolean() ? new Man() : new Woman();
        // 静态类型变化，human的静态类型是Human，也可以在使用时强转改变类型，但这个改变仍在编译期可知。
        StaticDispatch sr = new StaticDispatch();
        sr.sayHello((Man) human);
        sr.sayHello((Woman) human);
    }

    // 使用哪个重载版本，取决于传入参数的数量和数据类型。
    // 虚拟机(或准确地说是编译器)在重载时是通过参数的静态类型作为判定依据的，并把这个方法的符号引用写到了main()方法里的两条invokevirtual指令的参数中
    private static void test() {
        Human man = new Man();
        Human woman = new Woman();
        StaticDispatch sr = new StaticDispatch();
        sr.sayHello(man);
        sr.sayHello(woman);
    }
}
