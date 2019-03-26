package com.wolf.concurrenttest.program.serverclient.concurrent;

import java.util.concurrent.TimeUnit;

/**
 * Description: 10s执行一次
 *
 * @author 李超
 * @date 2019/02/20
 */
public class LogTask implements Runnable {

    @Override
    public void run() {

        try {
            while (Thread.currentThread().isInterrupted()) {
                TimeUnit.SECONDS.sleep(10);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Logger.writeLogs();
    }
}
