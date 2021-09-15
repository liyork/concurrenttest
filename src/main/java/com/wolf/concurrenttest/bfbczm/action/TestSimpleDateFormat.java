package com.wolf.concurrenttest.bfbczm.action;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Description: SimpleDateFormat线程不安全
 * 抛出异常NumberFormatException
 * Created on 2021/9/13 1:41 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class TestSimpleDateFormat {
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                try {
                    System.out.println(sdf.parse("2021-11-11 11:11:11"));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
