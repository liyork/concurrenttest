package com.wolf.concurrenttest.underjvm.classload.inovketmpcode;

import java.io.FileInputStream;

/**
 * Description: 主运行类
 * Created on 2021/7/28 10:15 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class MainClass {
    public static void main(String[] args) throws Exception {
        FileInputStream is = new FileInputStream("/Users/chaoli/intellijWrkSpace/concurrenttest/target/classes/com/wolf/concurrenttest/underjvm/classload/inovketmpcode/DynamicRunClass.class");
        byte[] b = new byte[is.available()];
        is.read(b);
        is.close();

        String execute = JavaclassExecutor.execute(b);
        System.out.println(execute);
    }
}
