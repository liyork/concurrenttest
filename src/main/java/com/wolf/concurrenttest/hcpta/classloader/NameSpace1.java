package com.wolf.concurrenttest.hcpta.classloader;

/**
 * Description: 不同类加载器加载同一个class
 * Created on 2021/9/21 3:18 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class NameSpace1 {
    public static void main(String[] args) throws ClassNotFoundException {
        MyClassLoader classLoader1 = new MyClassLoader("/Users/chaoli/intellijWrkSpace/concurrenttest/src/main/java", null);
        BrokerDelegateClassLoader classLoader2 = new BrokerDelegateClassLoader("/Users/chaoli/intellijWrkSpace/concurrenttest/src/main/java", null);

        Class<?> aClass = classLoader1.loadClass("com.wolf.concurrenttest.hcpta.classloader.HelloWorld");
        Class<?> bClass = classLoader2.loadClass("com.wolf.concurrenttest.hcpta.classloader.HelloWorld");

        System.out.println(aClass.getClassLoader());
        System.out.println(bClass.getClassLoader());
        System.out.println(aClass.hashCode());
        System.out.println(bClass.hashCode());
        System.out.println(aClass == bClass);
    }
}
