package com.wolf.concurrenttest.program.dataselect.concurrent;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/03/06
 */
public class TaskManager {

    private Set<RecursiveTask> tasks;
    private AtomicBoolean cancelled;

    public TaskManager() {
        this.tasks = ConcurrentHashMap.newKeySet();
        this.cancelled = new AtomicBoolean(false);
    }

    public void addTask(RecursiveTask task) {
        tasks.add(task);
    }

    //取消其他任务
    //情景：findAny找到结果、findAny或findAll某个任务出现未校验异常
    public void cancelTasks(RecursiveTask sourceTask) {

        if (cancelled.compareAndSet(false, true)) {
            for (RecursiveTask task : tasks) {
                if (task != sourceTask) {
                    if (cancelled.get()) {//todo 这里还能变成false？
                        task.cancel(true);
                    } else {
                        tasks.add(task);
                    }
                }
            }
        }
    }

    public void deleteTask(RecursiveTask task) {
        tasks.remove(task);
    }
}
