package com.wolf.concurrenttest.jvm.loadclass;

/**
 * Description:用来打jar用的类
 * <br/> Created on 2018/1/22 9:45
 *
 * @author 李超
 * @since 1.0.0
 */
public class Hot2 {

    public void hot() {
        System.out.println(" hot2 version 2 : " + this.getClass().getClassLoader());
    }

    public static void main(String[] args) {
        System.out.println("Hot2 is running...");
    }
}
