package com.wolf.concurrenttest.hcpta.classloader;

/**
 * Description:
 * Created on 2021/9/21 9:09 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class BootstrapClassLoader {
    public static void main(String[] args) {
        System.out.println("Bootstrap: " + String.class.getClassLoader());
        // 根加载器加载路径
        System.out.println(System.getProperty("sun.boot.class.path"));
    }
}
