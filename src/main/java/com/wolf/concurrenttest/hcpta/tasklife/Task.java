package com.wolf.concurrenttest.hcpta.tasklife;

/**
 * Description: 承载任务的逻辑执行单元
 * Created on 2021/9/23 1:27 PM
 *
 * @author 李超
 * @version 0.0.1
 */
@FunctionalInterface
public interface Task<T> {
    // 任务执行
    T call();
}
