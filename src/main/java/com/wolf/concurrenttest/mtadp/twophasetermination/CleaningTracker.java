package com.wolf.concurrenttest.mtadp.twophasetermination;

import java.io.Closeable;
import java.io.IOException;
import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;

/**
 * Description: 当Closeable实例对象被回收时，最后一次最大限度保证底层资源释放
 * <p>
 * <p>
 * 插曲：本来还想自己定义接口Closeable，但一想不对，这样就是自己新构造对象Closeable回收时触发，再一看socket本身就实现Closeable接口。。
 * jdk都已经想好了。。
 *
 * @author 李超
 * @date 2019/02/06
 */
public class CleaningTracker {

    private static final ReferenceQueue<Closeable> queue = new ReferenceQueue<>();

    static {
        Thread thread = new Thread(new Cleaner(), "CleaningTracker");
        thread.setDaemon(true);//守护线程
        thread.start();
    }

    public static void track(Closeable closeable) {
        new Tracker(closeable, queue);
    }

    //从queue中取出tracker然后进行关闭清理动作
    private static class Cleaner implements Runnable {

        @Override
        public void run() {

            for (; ; ) {
                try {
                    Tracker tracker = (Tracker) queue.remove();
                    tracker.close();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //继承PhantomReference，对socket进行一下存放用于后期close
    private static class Tracker extends PhantomReference<Closeable> {

        private final Closeable closeable;

        Tracker(Closeable closeable, ReferenceQueue<? super Closeable> queue) {

            super(closeable, queue);
            this.closeable = closeable;
        }

        public void close() {

            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
