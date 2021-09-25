package com.wolf.concurrenttest.hcpta.future;

/**
 * Description: 回调接口
 * Created on 2021/9/24 10:29 PM
 *
 * @author 李超
 * @version 0.0.1
 */
@FunctionalInterface
public interface Callback<T> {
    // 任务执行完会调用此方法，传递结果
    void call(T t);
}
