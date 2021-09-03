package com.wolf.concurrenttest.bfbczm.foundation;

/**
 * Description: runnable测试
 * Created on 2021/9/1 10:34 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class RunableTask implements Runnable {
    @Override
    public void run() {
        System.out.println("I am a child thread");
    }

    public static void main(String[] args) {
        RunableTask task = new RunableTask();
        new Thread(task).start();
        new Thread(task).start();
    }
}
