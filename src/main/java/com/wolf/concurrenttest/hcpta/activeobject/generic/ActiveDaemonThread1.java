package com.wolf.concurrenttest.hcpta.activeobject.generic;

/**
 * Description:
 * Created on 2021/9/26 10:11 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ActiveDaemonThread1 extends Thread {
    private final ActiveMessageQueue1 queue;

    public ActiveDaemonThread1(ActiveMessageQueue1 queue) {
        super("ActiveMessageThread1");
        this.queue = queue;
        setDaemon(true);
    }

    @Override
    public void run() {
        for (; ; ) {
            ActiveMessage activeMessage = this.queue.take();
            activeMessage.execute();
        }
    }
}
