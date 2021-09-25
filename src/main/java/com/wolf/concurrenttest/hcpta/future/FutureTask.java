package com.wolf.concurrenttest.hcpta.future;

/**
 * Description: 任务结果查看器
 * Created on 2021/9/24 10:11 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class FutureTask<T> implements Future<T> {
    private T result;
    private boolean isDone = false;
    private final Object LOCK = new Object();

    @Override
    public T get() throws InterruptedException {
        synchronized (LOCK) {
            // 任务没完成，则阻塞
            while (!isDone) {
                LOCK.wait();
            }
            return result;
        }
    }

    // 设定计算结果
    protected void finish(T result) {
        synchronized (LOCK) {
            // balking设计模式
            if (isDone) {
                return;
            }
            // 计算完成
            this.result = result;
            this.isDone = true;
            LOCK.notifyAll();
        }
    }

    @Override
    public boolean done() {
        return isDone;  // todo 是否有可见性问题?
    }
}
