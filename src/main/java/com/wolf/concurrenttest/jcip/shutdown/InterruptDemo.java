package com.wolf.concurrenttest.jcip.shutdown;

import java.util.concurrent.TimeUnit;

/**
 * Description: 处理InterruptedException方式：a.继续向上传播，b.或者catch并存储线程当前状态
 * Created on 2021/6/29 6:47 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class InterruptDemo {
    static class TaskRunnable implements Runnable {
        @Override
        public void run() {
            try {
                processTask();
            } catch (InterruptedException e) {// 这里也由于在runnable中不能将异常直接向上传递。catch异常后状态被清空，需要重新设定。可能用于当前线程下次感知到
                System.out.println(Thread.currentThread().isInterrupted());
                // restore interrupted status
                Thread.currentThread().interrupt();
                System.out.println(Thread.currentThread().isInterrupted());
            }
        }

        private void processTask() throws InterruptedException {
            TimeUnit.SECONDS.sleep(3);
        }
    }

    static class InterruptRunnable implements Runnable {
        private Thread thread;

        public InterruptRunnable(Thread thread) {
            this.thread = thread;
        }

        @Override
        public void run() {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            thread.interrupt();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        TaskRunnable taskRunnable = new TaskRunnable();
        Thread thread = new Thread(taskRunnable);
        thread.start();

        new Thread(new InterruptRunnable(thread)).start();
        TimeUnit.SECONDS.sleep(3);
        System.out.println("main look thread isAlive: " + thread.isAlive());
        System.out.println("main look thread isInterrupted: " + thread.isInterrupted());// 不存活，连interrupted也被清理了
    }
}
