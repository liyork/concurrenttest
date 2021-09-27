package com.wolf.concurrenttest.hcpta.activeobject.generic;

import java.util.LinkedList;

/**
 * Description: 更通用
 * Created on 2021/9/26 10:09 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ActiveMessageQueue1 {

    private final LinkedList<ActiveMessage> messages = new LinkedList<>();

    public ActiveMessageQueue1() {
        new ActiveDaemonThread1(this).start();
    }

    public void offer(ActiveMessage activeMessage) {
        synchronized (this) {
            messages.addLast(activeMessage);
            // 因为只有一个线程负责take
            this.notify();
        }
    }

    protected ActiveMessage take() {
        synchronized (this) {
            while (messages.isEmpty()) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return messages.removeFirst();
        }
    }
}
