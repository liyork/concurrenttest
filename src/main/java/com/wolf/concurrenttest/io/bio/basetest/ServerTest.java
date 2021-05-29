package com.wolf.concurrenttest.io.bio.basetest;

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
public class ServerTest {

    public static void main(String args[]) throws IOException {

        int port = 8899;
        ServerSocket server = new ServerSocket(port);
        System.out.println("11");
        Socket socket = server.accept();//卡住
        System.out.println("22");
        Reader reader = new InputStreamReader(socket.getInputStream());
        char chars[] = new char[64];
        int len;
        StringBuilder sb = new StringBuilder();
        System.out.println("33");
        //read是阻塞的，所以都读完了，就会等待这里了，除非client关闭了流
//        while ((len=reader.read(chars)) != -1) {
//            sb.append(new String(chars, 0, len));
//        }
        String temp;
        int index;
        while ((len = reader.read(chars)) != -1) {//这种肯定会有tcp粘包现象，不过只是简单测试而已。
            temp = new String(chars, 0, len);
            System.out.println("temp:" + temp);
            if ((index = temp.indexOf("eof")) != -1) {//遇到eof时就结束接收
                sb.append(temp.substring(0, index));
                break;
            }
            sb.append(temp);
        }


        System.out.println("from client: " + sb);
        Writer writer = new OutputStreamWriter(socket.getOutputStream());
        writer.write("Hello Client.");
        writer.flush();
        writer.close();
        reader.close();
        socket.close();
        server.close();
        System.out.println("44");
    }
}
