package com.wolf.concurrenttest.mtadp.observethread;

/**
 * Description: 任务生命周期接口，用于线程执行任务期间进行调用，进行通知
 *
 * @author 李超
 * @date 2019/01/31
 */
public interface TaskLifeCycle<T> {

    void onStart(Thread thread);

    void onRunning(Thread thread);

    void onFinish(Thread thread, T result);

    void onError(Thread thread, Exception e);
}
