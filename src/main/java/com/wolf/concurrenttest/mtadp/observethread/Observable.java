package com.wolf.concurrenttest.mtadp.observethread;

/**
 * Description: 可观察者，定义基本接口
 *
 * 某个对象发生状态改变时需要通知第三方时，观察者模式特别合适。
 *
 * @author 李超
 * @date 2019/01/31
 */
public interface Observable {

    enum Cycle {
        STARTED,RUNNING,DONE, ERROR
    }

    Cycle getCurCycle();

    void start();

    void interrupt();

}
