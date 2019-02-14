package com.wolf.concurrenttest.mtadp.observethread;

/**
 * Description: 可观察任务执行状况的线程
 *
 * @author 李超
 * @date 2019/01/31
 */
public class ObservableThread<T> extends Thread implements Observable {

    private final TaskLifeCycle<T> taskLifeCycle;

    private final Task<T> task;

    private Cycle curCycle;

    public ObservableThread(Task<T> task) {
        this(new EmptyLifeCycle<>(), task);
    }

    public ObservableThread(TaskLifeCycle<T> taskLifeCycle, Task<T> task) {

        super();

        if (null == task) {
            throw new IllegalStateException("the task is required.");
        }

        this.taskLifeCycle = taskLifeCycle;
        this.task = task;
    }

    @Override
    public final void run() {//不允许子类再对其重写，防止子类重写，导致整个生命周期监控失效。

        this.update(Cycle.STARTED, null, null);

        try {
            this.update(Cycle.RUNNING, null, null);

            T result = this.task.call();
            this.update(Cycle.DONE, result, null);
        } catch (Exception e) {
            this.update(Cycle.ERROR, null, e);
        }
    }

    private void update(Cycle cycle, T result, Exception e) {

        this.curCycle = cycle;

        if (null == taskLifeCycle) {
            return;
        }

        try {
            switch (cycle) {
                case STARTED:
                    this.taskLifeCycle.onStart(currentThread());
                    break;
                case RUNNING:
                    this.taskLifeCycle.onRunning(currentThread());
                    break;
                case DONE:
                    this.taskLifeCycle.onFinish(currentThread(), result);
                    break;
                case ERROR:
                    this.taskLifeCycle.onError(currentThread(), e);
                    break;
            }
        } catch (Exception ex) {
            if (cycle == Cycle.ERROR) {//执行任务出现异常，直接抛出,向上传递
                throw ex;
            } else {
                ex.printStackTrace();//非ERROR，只打印，保持task正常运行不受影响
            }
        }
    }

    @Override
    public Cycle getCurCycle() {
        return this.curCycle;
    }
}
