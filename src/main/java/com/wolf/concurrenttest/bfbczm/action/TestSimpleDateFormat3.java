package com.wolf.concurrenttest.bfbczm.action;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Description: 用tl保证线程安全
 * Created on 2021/9/13 1:41 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class TestSimpleDateFormat3 {
    static ThreadLocal<DateFormat> safeSdf = ThreadLocal.withInitial(() -> {
        System.out.println(Thread.currentThread().getName());
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    });

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                try {
                    System.out.println(safeSdf.get().parse("2021-11-11 11:11:11"));
                } catch (ParseException e) {
                    e.printStackTrace();
                } finally {
                    // 使用完毕记得清楚，避免内存泄露
                    safeSdf.remove();
                }
            }).start();
        }
    }
}
