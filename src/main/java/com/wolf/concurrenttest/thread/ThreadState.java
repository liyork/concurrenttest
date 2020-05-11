package com.wolf.concurrenttest.thread;

/**
 * Description: 线程状态
 *
 * // 名为"BlockedThread-2" 的线程状态：TIMED_WAITING
 * "BlockedThread-2" #13 prio=5 os_prio=0 tid=0x00007fa8f0157800 nid=0x154f waiting on condition [0x00007fa8c962e000]
 *    java.lang.Thread.State: TIMED_WAITING (sleeping)
 *         at java.lang.Thread.sleep(Native Method)
 *         at test.ThreadState$Blocked.run(ThreadState.java:61)
 *         - locked <0x00000000d9408370> (a java.lang.Class for test.ThreadState$Blocked)
 *         at java.lang.Thread.run(Thread.java:748)
 *
 * // 名为"BlockedThread-1" 的线程状态：BLOCKED
 * "BlockedThread-1" #12 prio=5 os_prio=0 tid=0x00007fa8f0156000 nid=0x154e waiting for monitor entry [0x00007fa8c972f000]
 *    java.lang.Thread.State: BLOCKED (on object monitor)
 *         at test.ThreadState$Blocked.run(ThreadState.java:61)
 *         - waiting to lock <0x00000000d9408370> (a java.lang.Class for test.ThreadState$Blocked)
 *         at java.lang.Thread.run(Thread.java:748)
 *
 * // 名为"WaitingThread" 的线程状态：WAITING
 * "WaitingThread" #11 prio=5 os_prio=0 tid=0x00007fa8f0154000 nid=0x154d in Object.wait() [0x00007fa8c9830000]
 *    java.lang.Thread.State: WAITING (on object monitor)
 *         at java.lang.Object.wait(Native Method)
 *         - waiting on <0x00000000d9408560> (a java.lang.Class for test.ThreadState$Waiting)
 *         at java.lang.Object.wait(Object.java:502)
 *         at test.ThreadState$Waiting.run(ThreadState.java:46)
 *         - locked <0x00000000d9408560> (a java.lang.Class for test.ThreadState$Waiting)
 *         at java.lang.Thread.run(Thread.java:748)
 *
 * // 名为"TimeWaitingThread" 的线程状态：TIMED_WAITING
 * "TimeWaitingThread" #10 prio=5 os_prio=0 tid=0x00007fa8f0152800 nid=0x154c waiting on condition [0x00007fa8c9931000]
 *    java.lang.Thread.State: TIMED_WAITING (sleeping)
 *         at java.lang.Thread.sleep(Native Method)
 *         at test.ThreadState$TimeWaiting.run(ThreadState.java:31)
 *         at java.lang.Thread.run(Thread.java:748)
 *
 * // 名为"RunThread" 的线程状态：RUNNABLE
 * "RunThread" #9 prio=5 os_prio=0 tid=0x00007fa8f0150800 nid=0x154b runnable [0x00007fa8c9a32000]
 *    java.lang.Thread.State: RUNNABLE
 *         at java.io.FileOutputStream.writeBytes(Native Method)
 *         at java.io.FileOutputStream.write(FileOutputStream.java:326)
 *         at java.io.BufferedOutputStream.flushBuffer(BufferedOutputStream.java:82)
 *         at java.io.BufferedOutputStream.flush(BufferedOutputStream.java:140)
 *         - locked <0x00000000d942e020> (a java.io.BufferedOutputStream)
 *         at java.io.PrintStream.write(PrintStream.java:482)
 *         - locked <0x00000000d9404b30> (a java.io.PrintStream)
 *         at sun.nio.cs.StreamEncoder.writeBytes(StreamEncoder.java:221)
 *         at sun.nio.cs.StreamEncoder.implFlushBuffer(StreamEncoder.java:291)
 *         at sun.nio.cs.StreamEncoder.flushBuffer(StreamEncoder.java:104)
 *         - locked <0x00000000d9404ae8> (a java.io.OutputStreamWriter)
 *         at java.io.OutputStreamWriter.flushBuffer(OutputStreamWriter.java:185)
 *         at java.io.PrintStream.write(PrintStream.java:527)
 *         - eliminated <0x00000000d9404b30> (a java.io.PrintStream)
 *         at java.io.PrintStream.print(PrintStream.java:597)
 *         at java.io.PrintStream.println(PrintStream.java:736)
 *         - locked <0x00000000d9404b30> (a java.io.PrintStream)
 *         at test.ThreadState$RunThread.run(ThreadState.java:20)
 *         at java.lang.Thread.run(Thread.java:748)
 *
 * @author 李超
 * @date 2020/02/26
 */
public class ThreadState {

    public static void main(String[] args) {
        new Thread(new RunThread(), "RunThread").start();// 一直处于RUNNABLE线程
        new Thread(new TimeWaiting(), "TimeWaitingThread").start();// TIMED_WAITING线程
        new Thread(new Waiting(), "WaitingThread").start();// WAITING线程
        new Thread(new Blocked(), "BlockedThread-1").start();// 获取锁之后TIMED_WAITING
        new Thread(new Blocked(), "BlockedThread-2").start();// 获取不到锁，被阻塞BLOCKED
    }

    // 该线程一直运行
    static class RunThread implements Runnable {
        @Override
        public void run() {
            while (true) {
                System.out.println(1);
            }
        }
    }

    // 该线程不断地进行睡眠 TIMED_WAITING
    static class TimeWaiting implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 该线程在Waiting.class实例上等待 WAITING
    static class Waiting implements Runnable {
        @Override
        public void run() {
            while (true) {
                synchronized (Waiting.class) {
                    try {
                        Waiting.class.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    // 该线程在Blocked.class实例上加锁后，不会释放该锁
    static class Blocked implements Runnable {
        public void run() {
            synchronized (Blocked.class) {
                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}


