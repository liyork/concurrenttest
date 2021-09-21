package com.wolf.concurrenttest.hcpta.classloader;

/**
 * Description:
 * Created on 2021/9/21 9:15 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ApplicationClassLoader {
    public static void main(String[] args) {
        System.out.println(System.getProperty("java.class.path"));
        System.out.println(ApplicationClassLoader.class.getClassLoader());
    }
}
