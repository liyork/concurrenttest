package com.wolf.concurrenttest.mtadp.threadpermessage;

import com.wolf.concurrenttest.threadpool.customize.demo2.MyThreadPool;
import com.wolf.concurrenttest.threadpool.customize.demo2.ThreadPool;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Description: 每个tcp放入线程池处理
 *
 * telnet localhost 8888
 *
 * @author 李超
 * @date 2019/02/05
 */
public class ChatServer {

    private final int port;

    private ThreadPool threadPool;

    private ServerSocket serverSocket;

    public ChatServer(int port) {
        this.port = port;
    }

    public ChatServer() {
        this(8888);
    }

    public void startServer() throws IOException {

        this.threadPool = new MyThreadPool(1, 4, 2, 1000);//第5个客户端能建立连接进来但是不能回应消息.
        this.serverSocket = new ServerSocket(port);
        this.serverSocket.setReuseAddress(true);
        System.out.println("chat server is started and listen at port:" + port);
        this.listen();
    }

    private void listen() throws IOException {
        for (; ; ) {
            Socket client = serverSocket.accept();
            this.threadPool.execute(new ClientHandler(client));
        }
    }

    public static void main(String[] args) throws IOException {
        new ChatServer().startServer();
    }
}
