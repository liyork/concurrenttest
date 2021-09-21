package com.wolf.concurrenttest.hcpta.classloader;

/**
 * Description: 不论load多少次，都是同一份class对象
 * Created on 2021/9/21 3:14 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class NameSpace {
    public static void main(String[] args) throws ClassNotFoundException {
        ClassLoader systemLoader = NameSpace.class.getClassLoader();
        Class<?> aClass = systemLoader.loadClass("com.wolf.concurrenttest.hcpta.classloader.HelloWorld");
        Class<?> bClass = systemLoader.loadClass("com.wolf.concurrenttest.hcpta.classloader.HelloWorld");

        System.out.println(aClass.hashCode());
        System.out.println(bClass.hashCode());
        System.out.println(aClass == bClass);
    }
}
