package com.wolf.concurrenttest.hcpta.volatiletest;

/**
 * Description:
 * Created on 2021/9/23 7:10 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ThreadCloseable extends Thread {
    // 保证可见性
    private volatile boolean started = true;

    @Override
    public void run() {
        while (started) {
            // do work
        }
    }

    public void shutdown() {
        this.started = false;
    }
}
