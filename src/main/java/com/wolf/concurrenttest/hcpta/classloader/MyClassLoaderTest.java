package com.wolf.concurrenttest.hcpta.classloader;

import java.lang.reflect.Method;

/**
 * Description:
 * Created on 2021/9/21 9:27 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class MyClassLoaderTest {
    public static void main(String[] args) throws Exception {
        MyClassLoader classLoader = new MyClassLoader();
        Class<?> aClass = classLoader.loadClass("com.wolf.concurrenttest.hcpta.classloader.HelloWorld");
        System.out.println(aClass.getClassLoader());
        // 仅用类加载器loadClass，并不会导致类的主动初始化，它只是执行了类加载过程中的加载阶段。(加载、连接、初始化)

        // 主动使用类
        Object helloWorld = aClass.newInstance();
        System.out.println(helloWorld);
        Method welcomeMethod = aClass.getMethod("welcome");
        String result = (String) welcomeMethod.invoke(helloWorld);
        System.out.println("result: " + result);
    }
}
