package com.wolf.concurrenttest.underjvm.classload;

/**
 * Description: 局部变量表的槽复用对垃圾收集的影响
 * javac com/wolf/concurrenttest/underjvm/classload/AfectGc.java
 * java -verbose:gc com/wolf/concurrenttest/underjvm/classload/AfectGc
 * Created on 2021/7/26 1:03 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class AffectGc {

    public static void main(String[] args) {
        // 1
        // byte[] placeholder = new byte[64 * 1024 * 1024];

        // 2，加上{}，placeholder作用域被限制在{}内
        //{
        //    byte[] placeholder = new byte[64 * 1024 * 1024];
        //}

        // 3，placeholder可以被回收的根因：局部变量表中的变量槽是否还存有关于placeholder数组对象的引用。
        {
            byte[] placeholder = new byte[64 * 1024 * 1024];
        }
        int a = 0;

        System.gc();
    }
}
