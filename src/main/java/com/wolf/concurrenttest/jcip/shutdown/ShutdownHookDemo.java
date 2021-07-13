package com.wolf.concurrenttest.jcip.shutdown;

/**
 * Description: 演示ShutdownHook
 * jvm makes no guarantees on the order in which shutdown hooks are started.可能出现并发执行问题
 * if any application thread are still running at shutdown time, they continue to run concurrently with the shutdown process.
 * when all shutdown hooks have completed, the JVM may choose to run finalizers, an then halts.
 * the jvm makes no attempt to stop or interrupt any application threads that are still running at shutdown time; they are abruptly
 * terminated when the jvm eventually halts.强制停止。
 * if the shutdown hooks or finalizers don't complete, then the orderly shutdown process "hangs" and the jvm must be shut down abruptly.也强制停止
 * in an abrupt shutdown, the jvm is not required to do anything other than halt the jvm; shutdown hooks will not run.
 * hook线程要注意线程安全，并尽快完成，为避免线程安全，可以多个service都用一个保证安全和顺序
 * Created on 2021/7/4 9:13 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ShutdownHookDemo {

    // registering a shutdown hook to stop the loggingService，保证退出时文件被关闭
    public void start() {
        LogServiceLifecycle logServiceLifecycle = new LogServiceLifecycle();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                logServiceLifecycle.stop();
            } catch (InterruptedException ignored) {

            }
        }));
    }
}
