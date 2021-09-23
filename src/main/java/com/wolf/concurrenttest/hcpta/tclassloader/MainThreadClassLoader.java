package com.wolf.concurrenttest.hcpta.tclassloader;

/**
 * Description:
 * Created on 2021/9/22 6:31 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class MainThreadClassLoader {
    public static void main(String[] args) {
        System.out.println(Thread.currentThread().getContextClassLoader());
    }
}
