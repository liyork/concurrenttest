package com.wolf.concurrenttest.program.schedule.advanced;

import java.util.concurrent.RunnableScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/24
 */
public class NewsExecutor extends ScheduledThreadPoolExecutor {

    public NewsExecutor(int corePoolSize) {
        super(corePoolSize);
    }

    @Override
    protected <V> RunnableScheduledFuture<V> decorateTask(Runnable runnable, RunnableScheduledFuture<V> task) {

        System.out.println("decorateTask...");
        return new ExecutorTask<>(runnable, null, task, this);
    }
}
