package com.wolf.concurrenttest.program.schedule.advanced;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Description: 根据当前小时返回特定时间
 *
 * @author 李超
 * @date 2019/02/24
 */
public class Timer {

    public static long getPeriod() {

        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour >= 6 && hour <= 8) {
            return TimeUnit.MILLISECONDS.convert(1, TimeUnit.MINUTES);
        }
        if (hour >= 13 && hour <= 14) {
            return TimeUnit.MILLISECONDS.convert(1, TimeUnit.MINUTES);
        }
        if (hour >= 20 && hour <= 22) {
            return TimeUnit.MILLISECONDS.convert(1, TimeUnit.MINUTES);
        }
        return TimeUnit.MILLISECONDS.convert(2, TimeUnit.MINUTES);
    }
}
