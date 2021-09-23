package com.wolf.concurrenttest.hcpta.tasklife;

/**
 * Description: 可监控的任务的生命周期的线程
 * Created on 2021/9/23 1:28 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ObservableThread<T> extends Thread implements Observable {

    private final TaskLifecycle<T> lifecycle;

    private final Task<T> task;
    private Cycle cycle;

    public ObservableThread(Task<T> task) {
        this(new TaskLifecycle.EmptyLifecycle<>(), task);
    }

    public ObservableThread(TaskLifecycle<T> lifecycle, Task<T> task) {
        super();
        if (task == null) {
            throw new IllegalArgumentException("The task si required");
        }
        this.lifecycle = lifecycle;
        this.task = task;
    }

    // 让他专门监控业务执行单元的生命周期，将真正的业务逻辑执行单元交给一个task
    @Override
    public final void run() {  // 不允许子类再对其重写
        // 当执行线程逻辑单元时，分别触发相应的事件
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
        this.cycle = cycle;
        if (lifecycle == null) {
            return;
        }
        try {
            switch (cycle) {
                case STARTED:
                    this.lifecycle.onStart(currentThread());
                    break;
                case RUNNING:
                    this.lifecycle.onRunning(currentThread());
                    break;
                case DONE:
                    this.lifecycle.onFinish(currentThread(), result);
                    break;
                case ERROR:
                    this.lifecycle.onError(currentThread(), e);
                    break;
            }
        } catch (Exception ex) {  // 响应事件过程出现异常，捕获，并忽略，保证lifecycle的实现不影响任务的正确执行
            e.printStackTrace();
        }
        // 但若任务执行过程中出现错误并抛出异常，那么update不能忽略异常，要继续抛出，保持与call同样的意图
        // todo 感觉从中文上看，应该在这里，而不应该在上面catch中
        if (cycle == Cycle.ERROR) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Cycle getCycle() {
        return this.cycle;
    }
}
