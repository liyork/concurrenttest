package com.wolf.concurrenttest.hcpta.deepthread;

import java.util.concurrent.TimeUnit;

/**
 * Description:
 * Created on 2021/9/17 1:24 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class FlagThreadExit {
    static class MyTask extends Thread {
        private volatile boolean closed = false;

        @Override
        public void run() {
            System.out.println("i will start work");
            while (!closed && !isInterrupted()) {
                // working
            }
            System.out.println("i will be exiting");
        }

        public void close() {
            this.closed = true;
            this.interrupt();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        MyTask t = new MyTask();
        t.start();
        TimeUnit.SECONDS.sleep(1);
        System.out.println("system will be shutdown");
        t.close();
    }
}
