package com.wolf.concurrenttest.jcip.stop;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Description: 只用一次ExecutionService的场景
 * Created on 2021/7/4 5:51 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class OneShotExecutionService {
    // using a private Executor whose lifetime is bounded by a method call
    boolean checkMail(Set<String> hosts, long timeout, TimeUnit unit) throws InterruptedException {
        ExecutorService exec = Executors.newCachedThreadPool();
        final AtomicBoolean hasNewMail = new AtomicBoolean(false);
        try {
            for (String host : hosts) {
                exec.execute(() -> {
                    if (checkMail(host)) {
                        hasNewMail.set(true);
                    }
                });
            }
        } finally {
            exec.shutdown();
            // 等待所有task执行完
            exec.awaitTermination(timeout, unit);
        }
        return hasNewMail.get();
    }

    private boolean checkMail(String host) {
        return false;
    }
}
