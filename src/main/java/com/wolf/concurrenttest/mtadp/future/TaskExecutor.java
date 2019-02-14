package com.wolf.concurrenttest.mtadp.future;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/01
 */
public interface TaskExecutor<I, O> {

    //无返回值,可以调用get阻塞等到直到有结果
    MyFuture<?> submit(Runnable runnable);

    //有返回值
    MyFuture<O> submit(Task<I, O> task, I input);

    void submit(Task<I, O> task, I input, Callback<O> callback);

    static <T, R> TaskExecutor<T, R> newExecutor() {
        return new TaskExecutorImpl<>();
    }
}
