package com.wolf.concurrenttest.hcpta.latch;

import java.util.concurrent.TimeUnit;

/**
 * Description:
 * Created on 2021/9/25 3:52 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public abstract class Latch {

    // 控制多少个线程完成时，才能打开，为0打开
    protected int limit;

    public Latch(int limit) {
        this.limit = limit;
    }

    // 使线程一直等待，直到所有线程完成工作，允许被中断
    public abstract void await() throws InterruptedException;

    // 可超时的等待
    public abstract void await(TimeUnit unit, long time) throws InterruptedException, WaitTimeoutException;

    // 任务线程完成时调用此方法，limit--
    public abstract void countDown();

    // 获取当前还有多少个线程没有完成任务
    public abstract int getUnarrived();
}
