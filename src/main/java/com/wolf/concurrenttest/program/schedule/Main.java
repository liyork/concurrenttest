package com.wolf.concurrenttest.program.schedule;

import com.wolf.concurrenttest.program.schedule.advanced.AdvancedNewsSystem;
import com.wolf.concurrenttest.program.schedule.simple.NewsSystem;

import java.util.concurrent.TimeUnit;

/**
 * Description: NewsSystem是启动任务，开启NewsWriter线程作为buffer的读者然后写入磁盘，
 * 对每个路径开启一个定时任务(间隔1分钟)，读取route中内容转换成CommonInformationItem放入buffer
 * 1写多读，用的阻塞队列
 * 使用latch作为NewsSystem的停止标志
 *
 * @author 李超
 * @date 2019/02/22
 */
public class Main {

    public static void main(String[] args) {

        testSimple();
//        testAdvanced();
    }

    private static void testSimple() {
        NewsSystem system = new NewsSystem("/Users/lichao30/tmp/sources.txt");
        Thread thread = new Thread(system);
        thread.start();

        try {
            TimeUnit.MINUTES.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        system.shutdown();
    }

    private static void testAdvanced() {

        AdvancedNewsSystem system = new AdvancedNewsSystem("/Users/lichao30/tmp/sources.txt");
        Thread thread = new Thread(system);
        thread.start();

        try {
            TimeUnit.MINUTES.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        system.shutdown();
    }
}
