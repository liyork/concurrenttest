package com.wolf.concurrenttest.underjvm.classload;

/**
 * Description: 重载方法匹配优先级
 * 演示重载时目标方法选择的过程。
 * javac com/wolf/concurrenttest/underjvm/classload/Overload.java
 * javap -v com/wolf/concurrenttest/underjvm/classload/Overload
 * Created on 2021/7/27 12:16 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class Overload {
    // 1若注释掉，会发生一次自动类型转换，'a'除了可以代表一个字符，还可以代表数字97(字符'a'的Unicode数值为十进制97，因此参数类型为int的重载也合适
    //public static void sayHello(char arg) {
    //    System.out.println("hello char");
    //}

    // 2再注释int，发生两次自动类型转换，'a'转型为97后，进一步转型为97L，匹配long的重载
    // 自动类型可以发生多次，char>int>long>float>double顺序，不会匹配到byte和short类型，因为char到byte或short不安全
    //public static void sayHello(int arg) {
    //    System.out.println("hello int");
    //}

    // 3再注释long，这时发生了一次自动装箱，'a'被包装为Character
    //public static void sayHello(long arg) {
    //    System.out.println("hello long");
    //}

    // 3再注释Character，调用Serializable，是因为Serializable是Character类的一个接口，当自动装箱后找不到，但是找到了装箱类所实现的接口类型，所以又发生一次自动转型。
    // char可以转型为int，但是Character绝不会转型为Integer，它只能安全地转型为它实现的接口或父类。
    //public static void sayHello(Character arg) {
    //    System.out.println("hello Character");
    //}

    // 4再注释Serializable，这时char装箱后转型为父类了，若有多个父类，将在继承关系中从下网上开始搜索，越接上层的优先级越低。
    //public static void sayHello(Serializable arg) {
    //    System.out.println("hello Serializable");
    //}

    // 5再注释Object，用了char...，可见变长参数的重载优先级最低，这时'a'被当做一个char[]数组的元素。
    //public static void sayHello(Object arg) {
    //    System.out.println("hello Object");
    //}

    public static void sayHello(char... arg) {
        System.out.println("hello char...");
    }

    public static void main(String[] args) {
        sayHello('a');
    }
}
