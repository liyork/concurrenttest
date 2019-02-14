package com.wolf.concurrenttest.mtadp.activeobject.general;

import java.util.LinkedList;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/06
 */
public class ActiveMessageQueue {

    private final LinkedList<ActiveMessage> messages = new LinkedList<>();

    public void offer(ActiveMessage methodMessage) {

        synchronized (this) {
            messages.addLast(methodMessage);
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
