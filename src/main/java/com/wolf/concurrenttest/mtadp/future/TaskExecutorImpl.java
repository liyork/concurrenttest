package com.wolf.concurrenttest.mtadp.future;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/01
 */
public class TaskExecutorImpl<I, O> implements TaskExecutor<I, O> {

    private final static String FUTURE_THREAD_PREFIX = "FUTURE-";

    private final AtomicInteger nextCounter = new AtomicInteger(0);

    private String getNextName() {
        return FUTURE_THREAD_PREFIX + nextCounter.getAndIncrement();
    }

    @Override
    public MyFuture<?> submit(Runnable runnable) {

        MyFutureImpl<Void> futureImpl = new MyFutureImpl<>();

        //todo 线程池
        new Thread(() -> {
            runnable.run();//todo 错误处理，并回调
            futureImpl.finish(null);
        }, getNextName()).start();

        return futureImpl;
    }

    @Override
    public MyFuture<O> submit(Task<I, O> task, I input) {

        MyFutureImpl<O> futureImpl = new MyFutureImpl<>();
        new Thread(() -> {
            O result = task.exe(input);
            futureImpl.finish(result);
        }, getNextName()).start();

        return futureImpl;
    }

    @Override
    public void submit(Task<I, O> task, I input, Callback<O> callback) {

        MyFutureImpl<O> futureImpl = new MyFutureImpl<>();
        new Thread(() -> {
            O result = task.exe(input);
            futureImpl.finish(result);
            callback.call(result);
        }, getNextName()).start();
    }
}
