package com.wolf.concurrenttest.program.serverclient.advantage;

import com.wolf.concurrenttest.program.serverclient.concurrent.Logger;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Description: 自定义ThreadPoolExecutor，添加相关统计分析方法，优先级队列
 *
 * @author 李超
 * @date 2019/02/21
 */
public class ServerExecutor extends ThreadPoolExecutor {

    //临时记录任务执行开始时间
    private ConcurrentHashMap<Runnable, Date> startTimes;
    //<username,ExecutorStatistics>
    private ConcurrentHashMap<String, ExecutorStatistics> executorStatistics;

    private static int CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors();
    private static int MAXIMUM_POOL_SIZE = Runtime.getRuntime().availableProcessors();
    private static long KEEP_ALIVE_TIME = 10;
    private static RejectedTaskController REJECTED_TASK_CONTROLLER = new RejectedTaskController();

    public ServerExecutor() {
        super(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS,
                new PriorityBlockingQueue<>(), REJECTED_TASK_CONTROLLER);

        startTimes = new ConcurrentHashMap<>();
        executorStatistics = new ConcurrentHashMap<>();
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        startTimes.put(t, new Date());
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        ServerFutureTask<?> task = (ServerFutureTask<?>) r;
        ConcurrentCommand command = task.getCommand();
        if (t == null) {
            if (!task.isCancelled()) {
                Date startDate = startTimes.remove(r);
                Date endDate = new Date();
                long executionTime = endDate.getTime() - startDate.getTime();
                ExecutorStatistics statistics = this.executorStatistics.computeIfAbsent(command.getUsername(),
                        n -> new ExecutorStatistics());
                statistics.addExecutionTime(executionTime);
                statistics.addTask();
                ConcurrentServer.finishTask(command.getUsername(), command);
            } else {
                String message = "The task " + command.hashCode() + " of user" + command.getUsername() + " " +
                        "has been cancelled.";
                Logger.sendMessage(message);
            }
        } else {
            String message = "The exception " + t.getMessage() +
                    "has been thrown.";
            Logger.sendMessage(message);
        }
    }

    @Override
    protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
        return new ServerFutureTask<T>(runnable);
    }

    public void writeStatics() {
        for (Map.Entry<String, ExecutorStatistics> entry : executorStatistics.entrySet()) {
            String user = entry.getKey();
            ExecutorStatistics stats = entry.getValue();
            Logger.sendMessage(user + ":" + stats);
        }
    }
}
