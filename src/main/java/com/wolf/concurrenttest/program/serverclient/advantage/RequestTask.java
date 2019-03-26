package com.wolf.concurrenttest.program.serverclient.advantage;

import com.wolf.concurrenttest.program.serverclient.concurrent.ConcurrentServer;
import com.wolf.concurrenttest.program.serverclient.concurrent.Logger;
import com.wolf.concurrenttest.program.serverclient.concurrent.ParallelCache;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

/**
 * Description: 单线程执行，从队列拉取clientsocket然后组装命令交给线程池执行。
 *
 * @author 李超
 * @date 2019/02/20
 */
public class RequestTask implements Runnable {

    private LinkedBlockingDeque<Socket> pendingConnections;
    private ServerExecutor executor = new ServerExecutor();
    private ConcurrentMap<String, ConcurrentMap<ConcurrentCommand, ServerFutureTask<?>>> taskController;

    public RequestTask(LinkedBlockingDeque<Socket> pendingConnections, ConcurrentMap<String, ConcurrentMap<ConcurrentCommand, ServerFutureTask<?>>> taskController) {
        this.pendingConnections = pendingConnections;
        this.taskController = taskController;
    }

    @Override
    public void run() {

        while (!Thread.currentThread().isInterrupted()) {

            try {
                Socket clientSocket = pendingConnections.take();
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                String line = in.readLine();
                Logger.sendMessage(line);

                ParallelCache cache = ConcurrentServer.getCache();
                String ret = cache.get(line);

                if (ret == null) {
                    ConcurrentCommand command;

                    String[] commandData = line.split(";");
                    System.out.println("Command:" + commandData[0]);
                    switch (commandData[0]) {
                        case "q":
                            System.out.println("query");
                            command = new ConcurrentQueryCommand(commandData);
                            break;
                        case "r":
                            System.out.println("report");
                            command = new ConcurrentReportCommand(commandData);
                            break;
                        case "z":
                            System.out.println("stop");
                            command = new ConcurrentStopCommand(commandData);
                            break;
                        case "s":
                            System.out.println("status");
                            command = new ConcurrentStatusCommand(commandData);
                            break;
                        case "c":
                            System.out.println("cancel");
                            command = new ConcurrentCancelCommand(commandData);
                            break;
                        default:
                            System.out.println("error");
                            command = new ConcurrentErrorCommand(commandData);
                            break;
                    }

                    ServerFutureTask<?> controller = (ServerFutureTask<?>) executor.submit(command);
                    storeController(command.getUsername(), controller, command);
                } else {
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    System.out.println(ret);
                    clientSocket.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void storeController(String username, ServerFutureTask<?> controller, ConcurrentCommand command) {
        taskController.computeIfAbsent(username, k -> new ConcurrentHashMap<>())
                .putIfAbsent(command, controller);
    }

    public void shutdown() {

        String message = "Request Task: " + pendingConnections.size() + " pending connections.";
        Logger.sendMessage(message);
        executor.shutdown();
    }

    public void terminate() {
        try {
            executor.awaitTermination(1, TimeUnit.DAYS);
            executor.writeStatics();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
