package com.wolf.concurrenttest.hcpta.latch;

import java.util.concurrent.TimeUnit;

/**
 * Description:
 * Created on 2021/9/25 3:55 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class CountDownLatch extends Latch {
    private Runnable runnable;

    public CountDownLatch(int limit) {
        super(limit);
    }

    public CountDownLatch(int limit, Runnable runnable) {
        super(limit);
        this.runnable = runnable;
    }

    @Override
    public void await() throws InterruptedException {
        synchronized (this) {
            // 不断判断limit，为0退出
            while (limit > 0) {
                this.wait();
            }
        }
        if (null != runnable) {
            runnable.run();
        }
    }

    @Override
    public void await(TimeUnit unit, long time) throws InterruptedException, WaitTimeoutException {
        if (time <= 0) {
            throw new IllegalArgumentException("the time is invalid.");
        }
        long remainingNanos = unit.toNanos(time);  // 将time按照原来的TimeUnit转换为纳秒
        // 将在endNanos纳秒后超时
        final long endNanos = System.nanoTime() + remainingNanos;

        synchronized (this) {
            while (limit > 0) {
                // 将纳秒为单位的remainingNanos转换成millis，不足1毫秒则异常
                if (TimeUnit.NANOSECONDS.toMillis(remainingNanos) < 0) {
                    throw new WaitTimeoutException("the wait time over specify time");
                }
                // 等待remainingNanos转成mills的时间，过程中可能被中断，所以下面要重新计算
                this.wait(TimeUnit.NANOSECONDS.toMillis(remainingNanos));
                remainingNanos = endNanos - System.nanoTime();
            }
        }
        if (null != runnable) {
            runnable.run();
        }
    }

    @Override
    public void countDown() {
        synchronized (this) {
            if (limit <= 0) {
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
