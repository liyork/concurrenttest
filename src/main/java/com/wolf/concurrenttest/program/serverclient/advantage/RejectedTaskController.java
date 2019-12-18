package com.wolf.concurrenttest.program.serverclient.advantage;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Description: 拒绝策略
 * 执行线程不够时，队列满了，小心使用"直接使用用户的线程"执行任务，这个策略。
 *
 * @author 李超
 * @date 2019/02/21
 */
public class RejectedTaskController implements RejectedExecutionHandler {

    @Override
    public void rejectedExecution(Runnable task, ThreadPoolExecutor executor) {

        ConcurrentCommand command = (ConcurrentCommand) task;

        try (Socket clientSocket = command.getSocket();
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);) {

            String message = "The server is shutting down." +
                    " Your request can not be served." +
                    " Shutting Down: " +
                    executor.isShutdown() + ". Terminated: " +
                    executor.isTerminated() + ". Terminating: " +
                    executor.isTerminating();

            System.out.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
