package com.wolf.concurrenttest.hcpta.activeobject;

/**
 * Description:
 * Created on 2021/9/26 10:11 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ActiveDaemonThread extends Thread {
    private final ActiveMessageQueue queue;

    public ActiveDaemonThread(ActiveMessageQueue queue) {
        super("ActiveMessageThread");
        this.queue = queue;
        setDaemon(true);
    }

    @Override
    public void run() {
        for (; ; ) {
            MethodMessage methodMessage = this.queue.take();
            methodMessage.execute();
        }
    }
}
