package com.wolf.concurrenttest.hcpta.future;

/**
 * Description: 用于提交任务
 * Created on 2021/9/24 10:07 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public interface FutureService<IN, OUT> {
    // 提交任务，不需要返回值
    Future<?> submit(Runnable runnable);

    // 提交任务，需要返回值
    Future<OUT> submit(Task<IN, OUT> task, IN input);

    Future<OUT> submit(Task<IN, OUT> task, IN input,Callback<OUT> callback);

    // 用静态方法创建一个FutureService的实现
    static <T, R> FutureService<T, R> newService() {
        return new FutureServiceImpl<>();
    }
}
