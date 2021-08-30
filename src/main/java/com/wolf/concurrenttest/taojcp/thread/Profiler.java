package com.wolf.concurrenttest.taojcp.thread;

/**
 * Description: 应用ThreadLocal的场景
 * Created on 2021/8/27 12:19 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class Profiler {
    // 第一次get调用时会进行初始化(若没有set调用)，每个线程会调用一次
    private static final ThreadLocal<Long> TIME_THREADLOCAL = ThreadLocal.withInitial(System::currentTimeMillis);

    public static final void begin() {
        TIME_THREADLOCAL.set(System.currentTimeMillis());
    }

    public static final long end() {
        return System.currentTimeMillis() - TIME_THREADLOCAL.get();
    }

    public static void main(String[] args) {
        Profiler.begin();
        SleepUtils.second(1);
        System.out.println("Cost: " + Profiler.end() + " mills");
    }
}
