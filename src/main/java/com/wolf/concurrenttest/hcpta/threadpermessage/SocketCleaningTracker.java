package com.wolf.concurrenttest.hcpta.threadpermessage;

import java.io.IOException;
import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.net.Socket;

/**
 * Description: 对socket对象进行最大努力关闭
 * Created on 2021/9/26 12:25 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class SocketCleaningTracker {
    private static final ReferenceQueue<Object> queue = new ReferenceQueue<>();

    static {
        new Cleaner().start();
    }

    static void tracker(Socket socket) {
        new Tracker(socket, queue);
    }

    // 守护线程，从queue中remove然后尝试清理动作
    private static class Cleaner extends Thread {
        private Cleaner() {
            super("SocketCleaningTracker");
            setDaemon(true);
        }

        @Override
        public void run() {
            for (; ; ) {
                try {
                    // 当Tracker被垃圾收集器回收时才会加入Queue中
                    Tracker tracker = (Tracker) queue.remove();
                    tracker.close();
                } catch (InterruptedException e) {
                }
            }
        }
    }

    private static class Tracker extends PhantomReference<Object> {
        private final Socket socket;

        public Tracker(Socket socket, ReferenceQueue<? super Object> queue) {
            super(socket, queue);
            this.socket = socket;
        }

        public void close() {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
