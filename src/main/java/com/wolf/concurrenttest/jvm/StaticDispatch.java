package com.wolf.concurrenttest.jvm;

/**
 * Description:静态类型，实际类型
 * 编译器编译重载方法时是通过参数的静态类型而不是实际类型判断的。
 * 依赖静态类型来定位方法执行版本的分派动作称为静态分派。如果没有对应的类型参数，编译器可能进行向上转型。
 *
 * invokevirtual执行时查找实际类型，再调用。运行期根据实际类型确定方法执行版本的分派称动态分派
 *
 * 编译期根据静态变量(调用者、参数)分派，运行时解析根据实际对象调用invokevirtual
 * 鉴于每次都这么查找，而且这个频率还很高，jvm进行优化，在解析阶段会建立虚方法表，存储实际的方法入口
 * <br/> Created on 11/3/17 8:28 AM
 *
 * @author 李超
 * @since 1.0.0
 */
public class StaticDispatch {

    static abstract class Human {
        abstract void sayxx();
    }

    static class Man extends Human{
        void sayxx(){
            System.out.println("Man sayxx");
        }
    }

    static class Woman extends Human{
        void sayxx(){
            System.out.println("Woman sayxx");
        }
    }

    public void sayHello(Human guy) {
        System.out.println("hello ,guy!");
    }

    public void sayHello(Man man) {
        System.out.println("hello,gentleman!");
    }

    public void sayHello(Woman woman) {
        System.out.println("hello,lady!");
    }

    public static void main(String[] args) {
        Human man = new Man();
        Human woman = new Woman();

        StaticDispatch staticDispatch = new StaticDispatch();
        staticDispatch.sayHello(man);
        staticDispatch.sayHello(woman);


        man.sayxx();
        woman.sayxx();
        man = new Woman();
        man.sayxx();
    }
}
