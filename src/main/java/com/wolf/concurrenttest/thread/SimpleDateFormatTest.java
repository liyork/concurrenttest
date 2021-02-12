package com.wolf.concurrenttest.thread;

import com.thoughtworks.xstream.core.util.ThreadSafeSimpleDateFormat;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Description:
 * Date formats are not synchronized.
 * It is recommended to create separate format instances for each thread.
 * If multiple threads access a format concurrently, it must be synchronized
 * externally.
 * <p>
 * 有并发问题,共享了内部实例变量calendar,format/parse时calendar.setTime(date);有race condition
 * 解决方案：使用threadlocal解决，
 * 后来无意发现ThreadSafeSimpleDateFormat这个使用了池化技术实现的每个一个simpledateformat但是可以重用。
 * <br/> Created on 3/5/18 2:55 AM
 *
 * @author 李超
 * @since 1.0.0
 */
public class SimpleDateFormatTest {

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private static ThreadLocal<SimpleDateFormat> threadLocal = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            System.out.println("currentThread:" + Thread.currentThread().getName());
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };

    private static ThreadSafeSimpleDateFormat threadSafeSimpleDateFormat = new ThreadSafeSimpleDateFormat("yyyy-MM-dd", TimeZone.getDefault(), 10, 15, true);

    @Test
    public void testZone() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(sdf.format(Calendar.getInstance().getTime()));
        sdf.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        System.out.println(sdf.format(Calendar.getInstance().getTime()));
    }

    public static void main(String[] args) {
//        testError();
//        testSolve();
        testSolve2();
//        testSolve3();
    }

    private static void testError() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    String source = "2017-10-02";
                    Date parse = simpleDateFormat.parse(source);
                    System.out.println("parse：" + parse);
                    String format = simpleDateFormat.format(parse);
                    System.out.println("format：" + format);
                    if (!source.equals(format)) {
                        System.out.println("not thread safe,source:" + source + " ,format:" + format);
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

    // use threadLocal
    private static void testSolve() {
        Runnable runnable = () -> {
            try {
                String source = "2017-10-02";
                Date parse = threadLocal.get().parse(source);
                System.out.println("parse：" + parse);
                String format = threadLocal.get().format(parse);
                System.out.println("format：" + format);
                if (!source.equals(format)) {
                    System.out.println("thread safe,source:" + source + " ,format:" + format);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        };

        for (int i = 0; i < 3; i++) {
            new Thread(runnable).start();
        }
    }

    // use threadSafeSimpleDateFormat
    private static void testSolve2() {
        Runnable runnable = () -> {
            try {
                String source = "2017-10-02";
                Date parse = threadSafeSimpleDateFormat.parse(source);
                System.out.println("parse：" + parse);
                String format = threadSafeSimpleDateFormat.format(parse);
                System.out.println("format：" + format);
                if (!source.equals(format)) {
                    System.out.println("thread safe,source:" + source + " ,format:" + format);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        };

        for (int i = 0; i < 10; i++) {
            new Thread(runnable).start();
        }
    }

    @Test
    public void testDateTimeFormatter() {
        String dateStr = "2017-10-02";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(dateStr, formatter);
        System.out.println("date:" + date);

        LocalDateTime now = LocalDateTime.now();
        String nowStr = now.format(formatter);
        System.out.println(nowStr);

        System.out.println(date.format(formatter));
    }

    private static void testSolve3() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Runnable runnable = () -> {
            String source = "2017-10-02";
            LocalDate parse = LocalDate.parse(source, formatter);
            System.out.println("parse：" + parse);
            String format = parse.format(formatter);
            System.out.println("format：" + format);
            if (!source.equals(format)) {
                System.out.println("thread safe,source:" + source + " ,format:" + format);
            }
        };

        for (int i = 0; i < 10; i++) {
            new Thread(runnable).start();
        }
    }

}
