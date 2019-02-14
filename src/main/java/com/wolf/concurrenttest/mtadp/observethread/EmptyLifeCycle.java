package com.wolf.concurrenttest.mtadp.observethread;

/**
 * Description: 空实现，adapter
 *
 * @author 李超
 * @date 2019/01/31
 */
public class EmptyLifeCycle<T> implements TaskLifeCycle<T> {

    @Override
    public void onStart(Thread thread) {

    }

    @Override
    public void onRunning(Thread thread) {

    }

    @Override
    public void onFinish(Thread thread, T result) {

    }

    @Override
    public void onError(Thread thread, Exception e) {

    }
}
