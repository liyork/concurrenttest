package com.wolf.concurrenttest.hcpta.future;

/**
 * Description: 执行任务
 * Created on 2021/9/24 10:10 PM
 *
 * @author 李超
 * @version 0.0.1
 */
@FunctionalInterface
public interface Task<IN, OUT> {
    // 给定参数，经过计算，返回结果
    OUT get(IN input);
}
