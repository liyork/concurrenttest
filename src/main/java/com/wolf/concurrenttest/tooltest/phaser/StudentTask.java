package com.wolf.concurrenttest.tooltest.phaser;

import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/26
 */
public class StudentTask implements Runnable {

    private Phaser phaser;

    public StudentTask(Phaser phaser) {
        this.phaser = phaser;
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + "到达考试");
        phaser.arriveAndAwaitAdvance();//到达并等待一起执行下步，通知phaser并等待

        doExercise1();
        System.out.println(Thread.currentThread().getName() + "做第1题完成...");
        phaser.arriveAndAwaitAdvance();

        doExercise2();
        System.out.println(Thread.currentThread().getName() + "做第2题完成...");
        phaser.arriveAndAwaitAdvance();

        doExercise3();
        System.out.println(Thread.currentThread().getName() + "做第3题完成...");
//        phaser.arriveAndAwaitAdvance();
//        phaser.arriveAndAwaitAdvance();
//        phaser.arriveAndAwaitAdvance();
//        phaser.arriveAndAwaitAdvance();

    }

    private void doExercise1() {
        long duration = (long) (Math.random() * 5);
        try {
            System.out.println(Thread.currentThread().getName() + "做第1题时间..." + duration);
            TimeUnit.SECONDS.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void doExercise2() {
        long duration = (long) (Math.random() * 5);
        try {
            System.out.println(Thread.currentThread().getName() + "做第2题时间..." + duration);
            TimeUnit.SECONDS.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void doExercise3() {
        long duration = (long) (Math.random() * 5);
        try {
            System.out.println(Thread.currentThread().getName() + "做第3题时间..." + duration);
            TimeUnit.SECONDS.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
