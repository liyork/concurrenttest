package com.wolf.concurrenttest.bfbczm.foundation2;

/**
 * Description: 线程不安全测试，一个++value对应汇编多条指令
 * javap -c ThreadNotSafeCount
 * Created on 2021/9/3 8:58 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ThreadNotSafeCount {
    private Long value;

    public Long getValue() {
        return value;
    }

    public void inc() {
        ++value;
    }
}
