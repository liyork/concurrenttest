package com.wolf.concurrenttest.thread;

import com.thoughtworks.xstream.core.util.ThreadSafeSimpleDateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Description:
 * 有并发问题,应该是共享了内部实力变量calendar
 * 解决方案：使用threadlocal解决，
 * 后来无意发现ThreadSafeSimpleDateFormat这个使用了池化技术实现的每个一个simpledateformat但是可以重用。
 * <br/> Created on 3/5/18 2:55 AM
 *
 * @author 李超
 * @since 1.0.0
 */
public class SimpleDateFormatTest {

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private static ThreadLocal<SimpleDateFormat> threadLocal = new ThreadLocal<SimpleDateFormat>(){
        @Override
        protected SimpleDateFormat initialValue() {
            System.out.println("currentThread:"+ Thread.currentThread().getName());
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };

    private static ThreadSafeSimpleDateFormat threadSafeSimpleDateFormat = new ThreadSafeSimpleDateFormat("yyyy-MM-dd", TimeZone.getDefault(),10,15,true);

    public static void main(String[] args) {
//        testError();
//        testSolve();
        testSolve2();
    }

    private static void testError() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    String source = "2017-10-02";
                    Date parse = simpleDateFormat.parse(source);
                    System.out.println("parse："+parse);
                    String format = simpleDateFormat.format(parse);
                    System.out.println("format："+format);
                    if (!source.equals(format)) {
                        System.out.println("not thread safe,source:"+source+" ,format:"+format);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        };

        for (int i = 0; i < 3; i++) {
            new Thread(runnable).start();
        }
    }

    private static void testSolve() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    String source = "2017-10-02";
                    Date parse = threadLocal.get().parse(source);
                    System.out.println("parse："+parse);
                    String format = threadLocal.get().format(parse);
                    System.out.println("format："+format);
                    if (!source.equals(format)) {
                        System.out.println("not thread safe,source:"+source+" ,format:"+format);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        };

        for (int i = 0; i < 3; i++) {
            new Thread(runnable).start();
        }
    }


    private static void testSolve2() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    String source = "2017-10-02";
                    Date parse = threadSafeSimpleDateFormat.parse(source);
                    System.out.println("parse："+parse);
                    String format = threadSafeSimpleDateFormat.format(parse);
                    System.out.println("format："+format);
                    if (!source.equals(format)) {
                        System.out.println("not thread safe,source:"+source+" ,format:"+format);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        };

        for (int i = 0; i < 10; i++) {
            new Thread(runnable).start();
        }
    }
}
