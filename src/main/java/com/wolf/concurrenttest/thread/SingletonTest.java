package com.wolf.concurrenttest.thread;

import java.io.*;

/**
 * Description:
 * 推荐使用内部类方式1
 * 如使用双重检查需保证4方式,不仅可以对静态字段，也能对实例变量延迟初始化，而方式1则只能对静态字段。不过一般工厂类都提供静态方法。
 * 3有问题
 *
 * 方式0的问题在于，若使用a变量，也会加载初始化类，也即加载了静态字段instance
 *
 * 延迟加载，需要时才初始化，但是增加了访问的开销。多数情况下，正常初始化优于延迟初始化。需要权衡。
 *
 * <br/> Created on 2016/8/11 16:52
 *
 * @author 李超()
 * @since 1.0.0
 */
public class SingletonTest implements Serializable{

    //方式0
    //static int a ;
//    private static SingletonTest instance = new SingletonTest();


    //构造函数私有化
    private SingletonTest() {
        System.out.println("construct...");
    }

    public static SingletonTest getInstance() {
        return SingletonTestHolder.instance;
    }

    //称为：initialization on demand holder idiom
    //因为jvm能保证类加载到线程使用前，执行类的初始化(静态域的初始化)，是带有锁执行的
    //方式1.第一次被使用时才会被jvm加载而且jvm保证内部类加载线程安全
    private static class SingletonTestHolder {
        //final 类型的域是不能修改的（但如果 final 域所引用的对象时可变的，那么这些被引用的对象是可以修改的）。
        // 然而，在 Java 内存模型中，final 域还有着特殊的语义。final 域能确保初始化过程的安全性，
        // 从而可以不受限制的访问不可变对象，并在共享这些对象时无需同步。
        //在并发当中，原理是通过禁止cpu的指令集重排序,来保证对象的安全发布，防止对象引用被其他线程在对象被完全构造完成前拿到并使用。
        //与前面介绍的锁和volatile相比较，对final域的读和写更像是普通的变量访问。对于final域，编译器和处理器要遵守两个重排序规则：
        //1. 在构造函数内对一个final域的写入，与随后把这个被构造对象的引用赋值给一个引用变量，这两个操作之间不能重排序。
        //2. 初次读一个包含final域的对象的引用，与随后初次读这个final域，这两个操作之间不能重排序。
        private final static SingletonTest instance = new SingletonTest();
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
//        testBase();
        testSeralize();
    }

    private static void testBase() {
        System.out.println("111");
        new Thread(new Runnable() {
            @Override
            public void run() {
               System.out.println(SingletonTest.getInstance().hashCode());
            }
        }).start();

        System.out.println(SingletonTest.getInstance().hashCode());
    }

    private static void testSeralize() throws IOException, ClassNotFoundException {
        SingletonTest instance = SingletonTest.getInstance();
        System.out.println(instance.hashCode());

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(bos);
        objectOutputStream.writeObject(instance);

        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ObjectInputStream objectInputStream = new ObjectInputStream(bis);
        Object o = objectInputStream.readObject();
        System.out.println(o.hashCode());

    }

    //=====双重检查锁，在现代已经被废弃了，推荐使用静态内部类，之所以出现的双重的原因是：早期的jvm，无竞争同步的执行速度很慢，jvm启动时很慢

    private static SingletonTest instance = null;

    //方式2.防止由于构造SingletonTest时未完全就被其他线程使用了。//可能也不管用。。。
    //双重检查对于是volatile或者非实例化的操作，可以节省性能
    public static SingletonTest getInstanceRight() {
        if(instance == null) {
            synchronized(SingletonTest.class) {
                SingletonTest temp = instance;
                if(temp == null) {
                    temp = new SingletonTest();
                    instance = temp;
                }
            }
        }
        return instance;
    }

    //A a = new A();一共执行3步，分配内存，调用构造方法初始化，分配引用。多线程遇到2、3步都有竞态条件。所以需要原子保护+可见性
    //方式3.由于构造SingletonTest时未完全有可能被其他线程使用了。
    public static SingletonTest getInstanceError() {
        if(instance == null) {
            synchronized(SingletonTest.class) {
                if(instance == null) {
                    instance = new SingletonTest();
                }
            }
        }
        return instance;
    }

    //方式4.getInstanceError方法+volatile SingletonTest 这是正确的

    //反序列化时调用，保持单例
    protected Object readResolve() throws ObjectStreamException {
        System.out.println("invoke readResolve");
        return SingletonTest.getInstance();
    }
}
