package com.wolf.concurrenttest.stdlibtest;

import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/24
 */
public class TimeUnitTest {

    public static void main(String[] args) {

        //Converts the given time duration in the given unit to this unit.
        long convert = TimeUnit.MILLISECONDS.convert(1, TimeUnit.MINUTES);
        System.out.println(convert);

        System.out.println("MILLISECONDS:" + SECONDS.toMillis(1L));
        System.out.println("MICROSECONDS:" + SECONDS.toMicros(1L));
        System.out.println("NANOSECONDS:" + SECONDS.toNanos(1L));
    }
}
