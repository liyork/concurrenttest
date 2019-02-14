package com.wolf.concurrenttest.mtadp.observethread;

/**
 * Description: 被执行任务
 *
 * @author 李超
 * @date 2019/01/31
 */
@FunctionalInterface
public interface Task<T> {

    T call();
}
