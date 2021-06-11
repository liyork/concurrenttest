package com.wolf.concurrenttest.jjzl.shutdown;

import ch.qos.logback.core.joran.action.ShutdownHookAction;
import ch.qos.logback.core.util.TimeUtil;
import sun.misc.Signal;

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
        //jdkGraceShutdown();

        //graceShutdownUseSignal();

        sequence();

        keepNotQuit();
    }

    // 信号先被接收，一定不能阻塞，否则后面的shutdownhook不能执行了。
    private static void sequence() throws InterruptedException {
        jdkGraceShutdown();
        graceShutdownUseSignal();
    }

    private static void keepNotQuit() {
        new Thread(() -> {
            try {
                TimeUnit.DAYS.sleep(Long.MAX_VALUE);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "non-Daemon-T").start();
    }

    // kill pid
    private static void graceShutdownUseSignal() throws InterruptedException {
        // 监听信号量并注册SignalHandler方式实现优雅退出
        Signal signal = new Signal(getOSSignalType());
        Signal.handle(signal, (Signal s) -> {
            System.out.println("signal -> " + s);
            try {
                TimeUnit.SECONDS.sleep(11110);
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
            System.exit(0);
        }, "ShutdownHookThread-"));
    }
}
