package com.wolf.concurrenttest.program.serverclient.advantage;

import com.wolf.concurrenttest.program.serverclient.concurrent.Logger;
import com.wolf.concurrenttest.program.serverclient.concurrent.ParallelCache;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.concurrent.*;

/**
 * Description: 高级并发服务器，带有cache、统计分析、优先级队列任务执行、取消任务
 *
 * @author 李超
 * @date 2019/02/20
 */
public class ConcurrentServer {

    private static ParallelCache cache;
    private static ServerSocket serverSocket;
    private static volatile boolean stopped = false;
    private static LinkedBlockingDeque<Socket> pendingConnections;
    //<username,command,servertask>
    private static ConcurrentMap<String, ConcurrentMap<ConcurrentCommand, ServerFutureTask<?>>> taskController;
    private static Thread requestThread;
    private static RequestTask task;

    public static void main(String[] args) throws IOException, InterruptedException {

        cache = new ParallelCache();
        Logger.initializeLog();
        pendingConnections = new LinkedBlockingDeque<>();
        taskController = new ConcurrentHashMap<>();
        task = new RequestTask(pendingConnections, taskController);
        requestThread = new Thread(task);
        requestThread.start();

        System.out.println("initialization completed.");

        serverSocket = new ServerSocket(8080);

        do {
            try {
                Socket clientSocket = serverSocket.accept();
                pendingConnections.push(clientSocket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } while (!stopped);

        finishServer();
        System.out.println("shutting down cache");
        cache.shutdown();
        System.out.println("cache ok");
    }

    private static void finishServer() {

        System.out.println("Shutting down the server...");
        task.shutdown();
        System.out.println("Shutting down Request task");
        requestThread.interrupt();
        System.out.println("Request task ok");
        System.out.println("Closing socket");
        System.out.println("Shutting down logger");
        Logger.sendMessage("Shutting down the logger");
        Logger.shutdown();
        System.out.println("Logger ok");
        System.out.println("Main server thread ended");
    }

    public static void shutdown() {

        stopped = true;

        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ParallelCache getCache() {
        return null;
    }

    public static void cancelTasks(String username) {

        ConcurrentMap<ConcurrentCommand, ServerFutureTask<?>> userTasks = taskController.get(username);
        if (null == userTasks) {
            return;
        }

        int taskNum = 0;
        Iterator<ServerFutureTask<?>> it = userTasks.values().iterator();
        while (it.hasNext()) {
            ServerFutureTask<?> task = it.next();
            ConcurrentCommand command = task.getCommand();
            if (!(command instanceof ConcurrentCancelCommand) && task.cancel(true)) {
                taskNum++;
                Logger.sendMessage("Task with code " + command.hashCode() +
                        " cancelled: " + command.getClass().getSimpleName());
                it.remove();
            }
        }
        String message = taskNum + " tasks has been cancelled.";
        Logger.sendMessage(message);
    }

    public static void finishTask(String username, ConcurrentCommand command) {

        ConcurrentMap<ConcurrentCommand, ServerFutureTask<?>> userTasks = taskController.get(username);
        userTasks.remove(command);
        String message = "Task with code " + command.hashCode() + " has finished";
        Logger.sendMessage(message);
    }
}
