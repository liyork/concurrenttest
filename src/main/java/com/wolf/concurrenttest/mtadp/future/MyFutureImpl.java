package com.wolf.concurrenttest.mtadp.future;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/01
 */
public class MyFutureImpl<T> implements MyFuture<T> {//todo futuretask将来的任务？

    private T result;

    private boolean isDone;

    private final Object LOCK = new Object();

    @Override
    public T get() throws InterruptedException {

        synchronized (LOCK) {
            while (!isDone) {
                LOCK.wait();
            }

            return result;
        }
    }

    public void finish(T result) {

        synchronized (LOCK) {
            if (isDone) {
                return;
            }

            this.result = result;
            this.isDone = true;
            LOCK.notifyAll();
        }
    }

    @Override
    public boolean isDone() {
        return isDone;
    }
}
