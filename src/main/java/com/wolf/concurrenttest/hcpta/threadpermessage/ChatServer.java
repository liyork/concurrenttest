package com.wolf.concurrenttest.hcpta.threadpermessage;

import com.wolf.concurrenttest.hcpta.threadpool.BasicThreadPool;
import com.wolf.concurrenttest.hcpta.threadpool.ThreadPool;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Description: 每个连接一个线程(线程池处理)
 * Created on 2021/9/25 9:27 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ChatServer {
    private final int port;
    private ThreadPool threadPool;

    private ServerSocket serverSocket;

    public ChatServer(int port) {
        this.port = port;
    }

    public ChatServer() {
        this(13312);
    }

    public void startServer() throws IOException {
        this.threadPool = new BasicThreadPool(1, 4, 2, 1000);
        this.serverSocket = new ServerSocket(port);
        this.serverSocket.setReuseAddress(true);
        System.out.println("Chat server is started and listen at port: " + port);
        this.listen();
    }

    private void listen() throws IOException {
        for (; ; ) {
            // 阻塞，直到有链接进入时，返回客户端的连接
            Socket client = serverSocket.accept();
            this.threadPool.execute(new ClientHandler(client));
        }
    }
}
