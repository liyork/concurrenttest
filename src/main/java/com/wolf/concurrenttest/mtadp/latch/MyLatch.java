package com.wolf.concurrenttest.mtadp.latch;

import java.util.concurrent.TimeUnit;

/**
 * Description: 多线程执行任务，汇聚到一起，进行汇报。
 * 指定阀门，所有条件满足才能放行。
 *
 * @author 李超
 * @date 2019/02/05
 */
public interface MyLatch {

    //一直等待直到条件满足被唤醒,可能被中断
    void await() throws InterruptedException;

    //带有超时时间的等待
    void await(TimeUnit unit, long time) throws InterruptedException;

    //部分条件满足时调用
    void countDown();
    //不一定准确，仅仅是一个当时快照
    int getUnarrived();
}
