package com.wolf.concurrenttest.netty.jjzl.shutdown;

import sun.misc.Signal;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Description:
 * Created on 2021/5/30 11:58 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class GraceShutdown {
    public static void main(String[] args) throws InterruptedException {
        jdkGraceShutdown();// shell执行kill pid时，可被可执行到，之后后jvm推出了。若kill -9则不执行hook

        //graceShutdownUseSignal();

        //sequence();

        keepNotQuit();
    }

    // 信号先被接收，一定不能阻塞sleep(11111)，否则后面的shutdownhook不能执行了。
    private static void sequence() throws InterruptedException {
        graceShutdownUseSignal();
        jdkGraceShutdown();
    }

    private static void keepNotQuit() {
        new Thread(() -> {
            try {
                TimeUnit.DAYS.sleep(Long.MAX_VALUE);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }, "non-Daemon-T").start();
    }

    // kill pid时，shell仅仅是发送一个信号通知，不会像GraceShutdown那样停止jvm。kill -9不会触发
    private static void graceShutdownUseSignal() throws InterruptedException {
        // 监听信号量并注册SignalHandler方式实现优雅退出
        Signal signal = new Signal(getOSSignalType());
        Signal.handle(signal, (Signal s) -> {
            System.out.println("signal -> " + s);
            try {
                TimeUnit.SECONDS.sleep(2);
                //TimeUnit.SECONDS.sleep(11111);// 模拟阻塞ShutdownHook
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    private static String getOSSignalType() {
        // win则选择SIGINT，接收ctrl+c，否则选择TERM信号，接收SIGTERM
        return System.getProperties().getProperty("os.name").
                toLowerCase().startsWith("win") ? "INT" : "TERM";
    }

    private static void jdkGraceShutdown() throws InterruptedException {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("ShutdownHook execute start...");
            System.out.println("Netty NioEventLoopGroup shutdownGracefully...");
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("ShutdownHook execute end...");
            try {
                TimeUnit.SECONDS.sleep(7);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Map<Thread, StackTraceElement[]> allStackTraces = Thread.getAllStackTraces();
            for (Map.Entry<Thread, StackTraceElement[]> entry : allStackTraces.entrySet()) {
                System.out.println(entry.getKey().getName());
            }
            //System.exit(0);// 这里不能调用这个，否则会出现不能退出，因为会阻塞在Shutdown.exit(Shutdown.java:212)
        }, "ShutdownHookThread-"));
    }
}
