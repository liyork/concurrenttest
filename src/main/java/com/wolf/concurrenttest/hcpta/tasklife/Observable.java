package com.wolf.concurrenttest.hcpta.tasklife;

/**
 * Description: 暴露给调用者使用
 * 仅定义需要对外的方法
 * Created on 2021/9/23 1:21 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public interface Observable {
    // 任务生命周期
    enum Cycle {
        STARTED, RUNNING, DONE, ERROR
    }

    // 获取当前任务的生命周期
    Cycle getCycle();

    // 定义线程启动方法，为了屏蔽Thread的其他方法
    void start();

    // 与start一样，也是为了屏蔽Thread的其他方法
    void interrupt();
}
