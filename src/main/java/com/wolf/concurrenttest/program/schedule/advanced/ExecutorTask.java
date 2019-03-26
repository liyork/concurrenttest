package com.wolf.concurrenttest.program.schedule.advanced;

import com.wolf.concurrenttest.program.schedule.simple.NewsTask;

import java.util.Date;
import java.util.concurrent.Delayed;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Description: 周期性任务的延迟时间随着一天中的时刻而改变,包装了原始构造的RunnableScheduledFuture，
 * 实际执行的方法都是自己的仅仅用task一些方法
 *
 * @author 李超
 * @date 2019/02/24
 */
public class ExecutorTask<V> extends FutureTask<V> implements RunnableScheduledFuture<V> {

    private RunnableScheduledFuture<V> task;
    private NewsExecutor executor;
    private long nextStartDate;
    private String name;

    public ExecutorTask(Runnable runnable, V result, RunnableScheduledFuture<V> task,
                        NewsExecutor executor) {
        super(runnable, result);
        this.task = task;
        this.executor = executor;
        this.name = ((NewsTask) runnable).getName();
        this.nextStartDate = new Date().getTime();
    }

    //根据自己定义的下次执行时间计算距下次执行还剩时间
    @Override
    public long getDelay(TimeUnit unit) {

        long remain;
        if (!isPeriodic()) {
            remain = task.getDelay(unit);
        } else {
            if (nextStartDate == 0) {
                remain = task.getDelay(unit);
            } else {
                Date now = new Date();
                remain = nextStartDate - now.getTime();
                remain = unit.convert(remain, TimeUnit.MILLISECONDS);
            }
        }

        return remain;
    }

    //用于放入队列先后顺序
    @Override
    public int compareTo(Delayed object) {

        return Long.compare(getNextStartDate(), ((ExecutorTask<V>) object).getNextStartDate());
    }

    @Override
    public boolean isPeriodic() {
        return task.isPeriodic();
    }

    //一旦执行则表明任务到时间了
    @Override
    public void run() {
        if (isPeriodic() && (!executor.isShutdown())) {
            super.runAndReset();
            Date now = new Date();
            nextStartDate = now.getTime() + Timer.getPeriod();
            executor.getQueue().add(this);
            System.out.println("Next Start Date: " + new Date(nextStartDate));
        }
    }

    public long getNextStartDate() {
        return nextStartDate;
    }
}
