package com.wolf.concurrenttest.jcip.stop;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Description: keeps track of cancelled tasks after shutdown(tasks started but not complete normally)
 * 被shutdownnow，追踪tasks started but did not complete. need to know not only which tasks didn't start, but
 * also which tasks were in progress when the executor was shut down.
 * the tasks must preserve the thread's interrupted status when they return, which well behaved tasks will do anyway
 * Created on 2021/7/4 6:06 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class TrackingExecutor {
    private final ExecutorService exec;
    private final Set<Runnable> taskCancelledAtShutdown = Collections.synchronizedSet(new HashSet<>());

    public TrackingExecutor(ExecutorService exec) {
        this.exec = exec;
    }

    public List<Runnable> getCancelledTasks() {
        if (!exec.isTerminated()) {
            throw new IllegalStateException("current exec is not terminated");
        }
        return new ArrayList<>(taskCancelledAtShutdown);
    }

    public void execute(final Runnable runnable) {
        exec.execute(() -> {
            try {
                runnable.run();
            } finally {
                // 执行过程中抛出异常，且当前exec是Shutdown，且当前线程是被interrupt
                if (isShutdown() && Thread.currentThread().isInterrupted()) {
                    taskCancelledAtShutdown.add(runnable);
                }
            }
        });
    }

    private boolean isShutdown() {
        return true;
    }

    public List<Runnable> shutdownNow() {
        return exec.shutdownNow();
    }

    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return exec.awaitTermination(timeout, unit);
    }
    // delegate other ExecutorService methods to exec
}
