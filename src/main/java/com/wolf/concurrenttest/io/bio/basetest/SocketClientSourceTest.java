package com.wolf.concurrenttest.io.bio.basetest;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Description: 测试socket的源码
 *
 * @author 李超
 * @date 2019/08/23
 */
public class SocketClientSourceTest {

    public static void main(String args[]) throws Exception {

        int connectionTimeout = 2000;
        Socket socket = new Socket();
        socket.setReuseAddress(true);
        socket.setKeepAlive(true);
        socket.setTcpNoDelay(true);
        socket.setSoLinger(true, 0);

        socket.connect(new InetSocketAddress("127.0.0.1", 8899), connectionTimeout);
        socket.setSoTimeout(2000);
        System.out.println("socket connect ...");

        OutputStream outputStream = socket.getOutputStream();
        outputStream.write("haha".getBytes(StandardCharsets.UTF_8));
        outputStream.write("\r\n".getBytes(StandardCharsets.UTF_8));
        outputStream.flush();

        System.out.println("11111");
    }
}
