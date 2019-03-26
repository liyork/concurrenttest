package com.wolf.concurrenttest.program.serverclient.concurrent;

import java.util.concurrent.TimeUnit;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/20
 */
public class CleanCacheTask implements Runnable {

    private final ParallelCache cache;

    public CleanCacheTask(ParallelCache cache) {
        this.cache = cache;
    }

    @Override
    public void run() {

        try {
            while (!Thread.currentThread().isInterrupted()) {

                TimeUnit.SECONDS.sleep(10);
                cache.cleanCache();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
