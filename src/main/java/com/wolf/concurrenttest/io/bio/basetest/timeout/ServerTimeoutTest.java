package com.wolf.concurrenttest.io.bio.basetest.timeout;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Description:
 * <br/> Created on 2017/5/9 13:55
 *
 * @author 李超
 * @since 1.0.0
 */
public class ServerTimeoutTest {

    public static void main(String args[]) throws IOException, InterruptedException {

        int port = 8899;
        ServerSocket server = new ServerSocket(port);
        System.out.println("11");
        Socket socket = server.accept();//卡住

        Thread.sleep(6000);
    }
}
