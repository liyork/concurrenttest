package com.wolf.concurrenttest.jcip.executordemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;

/**
 * Description: extends our web server with lifecycle support. add Shutdown support
 * Created on 2021/7/1 9:09 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class LifecycleWebServer extends AbstractWebServer {
    private static final Logger logger = LoggerFactory.getLogger(LifecycleWebServer.class);
    private final ExecutorService exec = null;

    public void start() throws IOException {
        ServerSocket socket = new ServerSocket(80);
        while (!exec.isShutdown()) {
            try {
                final Socket conn = socket.accept();
                exec.execute(() -> handleRequest(conn));
            } catch (RejectedExecutionException e) {
                if (!exec.isShutdown()) {
                    logger.warn("task shubmission rejected", e);
                }
            }
        }
    }

    public void stop() {
        exec.shutdown();
    }
}
