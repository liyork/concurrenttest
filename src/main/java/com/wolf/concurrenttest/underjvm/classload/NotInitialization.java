package com.wolf.concurrenttest.underjvm.classload;

/**
 * Description: 非主动使用类字段，演示
 * Created on 2021/7/24 5:39 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class NotInitialization {
    public static void main(String[] args) {
        // 演示1：对于静态字段，只有直接定义这个字段的类才会被初始化。
        // 置于是否触发子类的加载和验证阶段，没有明确固定，可以使用-XX:+TraceClassLoading查看
        //System.out.println(SubClass.value);

        // 演示2：通过数组定义来引用类，不会触发此类的初始化
        // 但会触发另一个名为"[Lcom.wolf.concurrenttest.underjvm.classload.SuperClass"的类的初始化，由虚拟机自动生成、直接继承于java.lang.Object子类，创建动作由字节码指令newarray触发
        // 这个类代表了一个元素类型为com.wolf.concurrenttest.underjvm.classload.SuperClass的一维数组，数组中印有该的属性和方法(length、clone)都是现在这个类里。
        //SuperClass[] superClasses = new SuperClass[10];
        //System.out.println(superClasses);

        // 演示3：常量在编译阶段会存入调用类的常量池中，本质上没有直接引用到定义常量的类，因此不会触发定义常量的类的初始化
        // 在编译阶段通过常量传播优化，已经将此常量的值"hello world"直接存储在NotInitialization类的常量池中。
        System.out.println(ConstClass.HELLOWORLD);
    }
}

class SuperClass {
    static {
        System.out.println("SuperClass init!");
    }

    public static int value = 123;
}

class SubClass extends SuperClass {
    static {
        System.out.println("SubClass init!");
    }
}

class ConstClass {
    static {
        System.out.println("ConstClass init!");
    }

    public static final String HELLOWORLD = "hello world";
}