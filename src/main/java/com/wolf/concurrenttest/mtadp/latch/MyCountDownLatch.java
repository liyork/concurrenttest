package com.wolf.concurrenttest.mtadp.latch;

import java.util.concurrent.TimeUnit;

/**
 * Description: await和countdown都使用limit，需要进行同步，否则可能会产生信号过早到来产生丢失(先到notify然后准备wait时可能
 * 就看不到limit也被再次唤醒不到。)，另外jdk方法也不允许没有锁调用wait和notifyall方法。
 * <p>
 * 当await超时时，已完成任务的线程自然结束，未完成的则不会被中断继续执行，latch只提供了门阀的功能，并不负责对线程的管理控制。
 * latch的作用是为了等待所有子任务完成后再执行其他任务。
 *
 * @author 李超
 * @date 2019/02/05
 */
public class MyCountDownLatch implements MyLatch {

    protected int limit;

    private Runnable callback;

    public MyCountDownLatch(int limit) {
        this.limit = limit;
    }

    //增加callback机制
    public MyCountDownLatch(int limit, Runnable callback) {
        this.limit = limit;
        this.callback = callback;
    }

    @Override
    public void await() throws InterruptedException {

        synchronized (this) {
            while (limit > 0) {//唤醒后再次判断
                this.wait();
            }
        }

        if (null != callback) {
            callback.run();
        }
    }

    @Override
    public void await(TimeUnit unit, long time) throws InterruptedException {

        if (time < 0) {
            throw new IllegalArgumentException("the time is invalid.");
        }

        //转换成纳秒
        long remainingNanos = unit.toNanos(time);
        //计算超时时间,在synchronized外计算，减少synchronized域，而且若是都在等待，那么理论上不应该获得锁再开始计算时间，
        //应该是方法调用时就计算超时时间，然后排队的过程中也算时间。
        long endNanos = System.nanoTime() + remainingNanos;

        synchronized (this) {
            while (limit > 0) {
                //超时抛出异常，应该是在synchronized外面等待过长
                if (TimeUnit.NANOSECONDS.toMillis(remainingNanos) <= 0) {//剩余不足1毫秒
                    throw new WaitTimeoutException("the wait time over specify time.");
                }

                this.wait(TimeUnit.NANOSECONDS.toMillis(remainingNanos));
                //被唤醒，重新计算
                remainingNanos = endNanos - System.nanoTime();
            }
        }

        if (null != callback) {
            callback.run();
        }
    }

    @Override
    public void countDown() {

        synchronized (this) {
            if (limit < 0) {
                throw new IllegalStateException("all of task already arrived");
            }
            limit--;
            this.notifyAll();
        }
    }

    @Override
    public int getUnarrived() {
        return limit;
    }
}
