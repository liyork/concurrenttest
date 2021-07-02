package com.wolf.concurrenttest.jcip.executordemo;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Description: using a thread Pool
 * submission of the request-handling task is decoupled from its execution using an Executor,
 * and its behavior can be changed merely by substituting a different Executor implementation.
 * <p>
 * Created on 2021/7/1 7:11 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class TaskExecutionWebServer extends AbstractWebServer {
    private static final int NTHREADS = 100;
    private static final Executor exec = Executors.newFixedThreadPool(NTHREADS);

    public void start() throws IOException {
        ServerSocket socket = new ServerSocket(80);
        while (true) {
            final Socket connection = socket.accept();
            Runnable task = () -> {
                handleRequest(connection);
            };
            exec.execute(task);
        }
    }

    public static void main(String[] args) throws IOException {
        new TaskExecutionWebServer().stop();
    }

    // 若要改成thread-per-task，上面基本不动，仅仅修改Executor的实现即可
    // Executor that Starts a New Thread for Each Task
    class ThreadPerTaskExecutor implements Executor {
        @Override
        public void execute(Runnable r) {
            new Thread(r).start();
        }
    }

    // single-thread version, Executor that Executes Task Synchronously in the Calling Thread
    class WithinThreadExecutor implements Executor {
        @Override
        public void execute(Runnable r) {
            r.run();
        }
    }
}
