package com.wolf.concurrenttest.tooltest.phaser;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/26
 */
public class Main {

    public static void main(String[] args) throws InterruptedException {
        MyPhaser phaser = new MyPhaser();
        StudentTask[] studentTask = new StudentTask[6];
        for (int i = 0; i < studentTask.length; i++) {
            studentTask[i] = new StudentTask(phaser);
            phaser.register();  //增加phaser中的线程计数
        }

        Thread[] threads = new Thread[studentTask.length];
        for (int i = 0; i < studentTask.length; i++) {
            threads[i] = new Thread(studentTask[i], "Student " + i);
            threads[i].start();
        }

        //等待所有线程执行结束
        for (int i = 0; i < studentTask.length; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Phaser has finished:" + phaser.isTerminated());
    }

}
