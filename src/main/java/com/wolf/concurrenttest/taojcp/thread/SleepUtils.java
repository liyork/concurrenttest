package com.wolf.concurrenttest.taojcp.thread;

import java.util.concurrent.TimeUnit;

/**
 * Description: 睡眠工具类
 * Created on 2021/8/26 1:56 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class SleepUtils {
    public static final void second(long seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
