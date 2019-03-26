package com.wolf.concurrenttest.program.serverclient.concurrent;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Description: 异步服务器，使用线程池，保证每个请求一个线程处理,命令可缓存
 *
 * @author 李超
 * @date 2019/02/20
 */
public class ConcurrentServer {

    private static ThreadPoolExecutor executor;
    private static ParallelCache cache;
    private static ServerSocket serverSocket;
    private static volatile boolean stopped = false;

    public static void main(String[] args) throws IOException, InterruptedException {

        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        cache = new ParallelCache();
        Logger.initializeLog();

        System.out.println("initialization completed.");

        serverSocket = new ServerSocket(8080);

        do {
            try {
                Socket clientSocket = serverSocket.accept();
                RequestTask task = new RequestTask(clientSocket);
                executor.execute(task);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } while (!stopped);

        executor.awaitTermination(1, TimeUnit.DAYS);
        System.out.println("shutting down cache");
        cache.shutdown();
        System.out.println("cache ok");

        System.out.println("main server thread ended");
    }

    public static void shutdown() {

        stopped = true;
        System.out.println("Shutting down the server...");
        System.out.println("Shutting down executor");
        executor.shutdown();
        System.out.println("Executor ok");
        System.out.println("Closing socket");
        try {
            serverSocket.close();
            System.out.println("Socket ok");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Shutting down logger");
        Logger.sendMessage("Shutting down the looger");
        Logger.shutdown();
        System.out.println("Logger ok");
    }

    public static ParallelCache getCache() {
        return null;
    }

    public static ThreadPoolExecutor getExecutor() {
        return executor;
    }
}
