package com.wolf.concurrenttest.program.serverclient.advantage;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Description: 执行分析，一共耗费多长时间，执行了多少任务
 *
 * @author 李超
 * @date 2019/02/21
 */
public class ExecutorStatistics {

    private AtomicLong executionTime = new AtomicLong(0L);
    private AtomicInteger numTasks = new AtomicInteger(0);

    public void addExecutionTime(long time) {
        executionTime.addAndGet(time);
    }

    public void addTask() {
        numTasks.incrementAndGet();
    }

    public AtomicLong getExecutionTime() {
        return executionTime;
    }

    public AtomicInteger getNumTasks() {
        return numTasks;
    }

    @Override
    public String toString() {
        return "Executed Tasks: " + getNumTasks() + ". Execution Time: " + getExecutionTime();
    }
}
