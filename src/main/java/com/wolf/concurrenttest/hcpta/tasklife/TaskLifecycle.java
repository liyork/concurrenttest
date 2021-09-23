package com.wolf.concurrenttest.hcpta.tasklife;

/**
 * Description:
 * Created on 2021/9/23 1:23 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public interface TaskLifecycle<T> {
    // 任务启动时触发
    void onStart(Thread thread);

    // 任务正在运行时触发
    // 不同于线程生命周期，若当前线程进入休眠或阻塞，那任务也是running
    void onRunning(Thread thread);

    // 任务运行结束时触发，result为结果
    void onFinish(Thread thread, T result);

    // 任务执行报错时触发
    void onError(Thread thread, Exception e);

    // 空实现，为了让使用者保持对Thread类的使用习惯
    class EmptyLifecycle<T> implements TaskLifecycle<T> {

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
}
