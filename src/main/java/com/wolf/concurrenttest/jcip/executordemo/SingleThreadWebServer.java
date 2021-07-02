package com.wolf.concurrenttest.jcip.executordemo;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Description: sequential web server
 * execute tasks sequentially in a single thread
 * 同一时间仅能处理一个请求。主线程处理接收连接和处理关联请求。
 * Created on 2021/6/30 10:18 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class SingleThreadWebServer extends AbstractWebServer {
    public void start() throws IOException {
        ServerSocket socket = new ServerSocket(80);
        while (true) {
            Socket connection = socket.accept();
            handleRequest(connection);
        }
    }

    public static void main(String[] args) throws IOException {
        new SingleThreadWebServer().start();
    }
}
