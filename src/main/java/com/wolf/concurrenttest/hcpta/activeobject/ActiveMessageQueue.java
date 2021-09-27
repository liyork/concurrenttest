package com.wolf.concurrenttest.hcpta.activeobject;

import java.util.LinkedList;

/**
 * Description: 队列
 * 无限制大小
 * Created on 2021/9/26 10:09 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ActiveMessageQueue {
    // 存放提交的MethodMessage消息
    private final LinkedList<MethodMessage> messages = new LinkedList<>();

    public ActiveMessageQueue() {
        new ActiveDaemonThread(this).start();
    }

    public void offer(MethodMessage methodMessage) {
        synchronized (this) {
            messages.addLast(methodMessage);
            // 因为只有一个线程负责take
            this.notify();
        }
    }

    protected MethodMessage take() {
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
