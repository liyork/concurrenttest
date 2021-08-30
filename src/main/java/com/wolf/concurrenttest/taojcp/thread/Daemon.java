package com.wolf.concurrenttest.taojcp.thread;

/**
 * Description: 测试Daemon线程的finally不会执行
 * 这个真要注意！
 * Created on 2021/8/26 9:59 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class Daemon {
    public static void main(String[] args) {
        Thread thread = new Thread(new DaemonRunner(), "DaemonRunner");
        thread.setDaemon(true);
        thread.start();
    }

    static class DaemonRunner implements Runnable {
        @Override
        public void run() {
            try {
                SleepUtils.second(10);
            } finally {
                System.out.println("DaemonThread finally run.");
            }
        }
    }
}
