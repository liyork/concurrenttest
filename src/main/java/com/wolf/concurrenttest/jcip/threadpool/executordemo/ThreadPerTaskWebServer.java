package com.wolf.concurrenttest.jcip.threadpool.executordemo;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Description: web server that Starts a New Thread for Each Request, thread-per-task, nothing places any limit on the number of threads created
 * has three main consequences:
 * + Task processing is offloaded from the main thread, enabling the main loop to resume waiting for the
 * nex incoming connection more quickly. enables new connections to be accepted before previous
 * requests complete, improving responsiveness
 * + Task can be porcessed in parallel, enabling multiple rquests to be serviced simultaneously. may improve
 * throughput if there are multiple processors, or if tasks need to block for any reason such as I/O completion,
 * lock acquisition, or resource availability.
 * + Task-handling code must be thread-safe, because it may be invoked concurrently for multiple tasks.
 * drawback:
 * + thread creation and terdown are not free.
 * + resource consumption. if you have enough threads to keep all the CPUs busy, creating more threads won't help
 * and may even hurt
 * + Stability. there is a limit on how many threads can be created. when hit this limit, the most likely
 * result is an OutOfMemoryError.
 * <p>
 * Created on 2021/6/30 10:39 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ThreadPerTaskWebServer extends AbstractWebServer {

    public void start() throws IOException {
        ServerSocket socket = new ServerSocket(80);
        while (true) {// main thread still alternates between accepting an incoming connection and dispatching the request.
            final Socket connection = socket.accept();
            Runnable task = () -> handleRequest(connection);
            new Thread(task).start();// for each connection create a new thread
        }
    }

    public static void main(String[] args) throws IOException {
        new ThreadPerTaskWebServer().start();
    }
}
