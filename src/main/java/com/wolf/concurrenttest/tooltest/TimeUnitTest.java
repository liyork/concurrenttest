package com.wolf.concurrenttest.tooltest;

import java.util.concurrent.TimeUnit;

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
    }
}
