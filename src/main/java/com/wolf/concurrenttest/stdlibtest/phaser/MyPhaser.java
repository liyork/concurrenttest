package com.wolf.concurrenttest.stdlibtest.phaser;

import java.util.concurrent.Phaser;

/**
 * Description: 为执行分为多阶段的算法的任务提供同步
 *
 * 像是countdownlatch重复利用,应该是提供高级功能
 * phaser和phase
 * 一个phaser可以包含一个或者多个phase.
 * party
 * 通过phaser同步的线程被称为party,party需要向phaser注册,该方法仅仅是增加phaser中的线程计数
 * arrived和unarrived
 * party到达一个phaser某个阶段之前处于unarrived状态,到达时处于arrived状态.一个arrived的party也被称为arrival.
 * deregister
 * 一个线程可以在到达(arrive)某个阶段(phase)后退出(deregister),此时可以使用arriveAndDeregister()方法.
 * phase计数
 * Phaser类有一个phase计数,初始阶段为0.当一个阶段的所有线程到达(arrive)时,会将phase计数加1,这个动作被称为advance.当这个计数达到Integer.MAX_VALUE时,会被重置为0,开始下一轮循环
 * onAdvance(int phase, int registeredParties)
 * 可以在这个方法中定义advance过程中需要执行何种操作,如果进入下一阶段(phase)执行,返回false.如果返回true,会导致phaser结束,因此该方法也是终止phaser的关键所在.
 *Tiering
 * Tiering即分层的意思.Phaser支持分层结构，即通过构造函数Phaser(Phaser parent)和Phaser(Phaser parent, int parties)构造一个树形结构。这有助于减轻因在单个的Phaser上注册过多的任务而导致的竞争，从而提升吞吐量，代价是增加单个操作的开销。
 *
 * api
 * arrive(): 通知phaser该线程已到达,并且不需等待其它线程,直接进入下一个执行阶段(phase)
 * arriveAndAwaitAdvance(): 通知phaser该线程已到达,并且等待其它线程.
 * arriveAndDeregister()，完成当前阶段并退出，下阶段不参与
 * awaitAdvance(int phase): 阻塞线程,直到phaser的phase计数从参数中的phase变化成为另一个值.比如awaitAdvance(2),会导致线程阻塞,直到phaser的phase计数变为3以后才会继续执行.
 * awaitAdvanceInterruptibly(int phase)
 * onAdvance(int phase, int registeredParties)
 * register(): phaser的线程计数加1.
 *
 * arriveAndAwaitAdvance都到了则执行onAdvance方法，onAdvance返回true标识结束
 *
 * 分段器的主要目的是使那些可以分割成多个阶段的算法以并发方式执行。所有任务完成当前阶段
 * 之前，任何任务都不能进入下一阶段
 *
 * 分段器有：激活状态、终止状态(onAdvance返回true或者所有任务调用arriveAndDeregister)
 *
 *
 * @author 李超
 * @date 2019/02/26
 */
public class MyPhaser extends Phaser {

    //在每个阶段执行完成后回调的方法
    @Override
    protected boolean onAdvance(int phase, int registeredParties) {

        switch (phase) {
            case 0:
                return studentArrived();
            case 1:
                return finishFirstExercise();
            case 2:
                return finishSecondExercise();
            case 3:
                return finishExam();
            default:
                System.out.println("1111111111:phase:"+phase);
                return false;
        }

    }

    private boolean studentArrived() {
        System.out.println("学生准备好了,学生人数：" + getRegisteredParties());
        return false;
    }

    private boolean finishFirstExercise() {
        System.out.println("第一题所有学生做完");
        return false;
    }

    private boolean finishSecondExercise() {
        System.out.println("第二题所有学生做完");
        return false;
    }

    private boolean finishExam() {
        System.out.println("第三题所有学生做完，结束考试");
        return true;
    }

}
