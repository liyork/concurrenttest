package com.wolf.concurrenttest.threadpool.customize;

import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;

/**
 * Description:使用wait+notify简单实现线程池。
 * <br/> Created on 3/8/18 8:39 PM
 *
 * @author 李超
 * @since 1.0.0
 */
public class MyThreadPool {

    private LinkedList<Runnable> runnables = new LinkedList<>();
    private LinkedList<Worker> workers = new LinkedList<>();

    MyThreadPool(){
        for (int i = 0; i < 2; i++) {
            Worker worker = new Worker();
            Thread thread = new Thread(worker,"MyThreadPool"+i);
            worker.setCurrentThread(thread);
            thread.start();
            workers.add(worker);
        }
    }

    public void execute(Runnable runnable) {
        synchronized (runnables) {
            runnables.add(runnable);
            runnables.notify();//唤醒单个线程，由于生产者没有竞争，所以不会产生假死
        }
    }

    public void stop() {
        for (Worker worker : workers) {
            worker.stop();
        }
    }

    private class Worker implements Runnable{

        private volatile boolean isStop;
        private Thread currentThread;

        public void setCurrentThread(Thread currentThread) {
            this.currentThread = currentThread;
        }

        @Override
        public void run() {
            while (!isStop && !Thread.currentThread().isInterrupted()) {
                synchronized (runnables) {
                    while (runnables.isEmpty()) {
                        try {
                            runnables.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            return;
                        }
                    }

                    Runnable runnable = runnables.removeFirst();
                    System.out.println(Thread.currentThread().getName()+" run");
                    runnable.run();
                }
            }
        }

        public void stop() {
            isStop = true;
            currentThread.interrupt();
        }
    }
}
