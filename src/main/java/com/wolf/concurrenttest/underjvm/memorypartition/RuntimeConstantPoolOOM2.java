package com.wolf.concurrenttest.underjvm.memorypartition;

/**
 * Description: jdk8常量池已经放在堆，所以str1第一次放入并获取，str2则是已存在并不放入
 * Created on 2021/7/17 1:14 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class RuntimeConstantPoolOOM2 {

    public static void main(String[] args) {
        String str1 = new StringBuilder("计算机").append("软件").toString();
        System.out.println(str1.intern() == str1);

        String str2 = new StringBuilder("ja").append("va").toString();
        System.out.println(str2.intern() == str2);
        System.out.println("java" == str2);
    }
}
