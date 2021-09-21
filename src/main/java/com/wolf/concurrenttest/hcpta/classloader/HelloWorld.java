package com.wolf.concurrenttest.hcpta.classloader;

/**
 * Description: 被MyClassLoader加载的类
 * javac HelloWorld.java
 * Created on 2021/9/21 9:26 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class HelloWorld {
    static {
        System.out.println("Hello World Class is Initialized");
    }

    public String welcome() {
        return "Hello World";
    }
}
