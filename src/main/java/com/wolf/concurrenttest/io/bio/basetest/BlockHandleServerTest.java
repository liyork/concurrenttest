package com.wolf.concurrenttest.io.bio.basetest;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Description:只能同步处理所有client请求
 * <br/> Created on 2017/5/9 14:12
 *
 * @author 李超
 * @since 1.0.0
 */
public class BlockHandleServerTest {

    public static void main(String args[]) throws IOException {
        int port = 8899;
        ServerSocket server = new ServerSocket(port);
        while(true) {
            Socket socket = server.accept();
            //跟客户端建立好连接之后，我们就可以获取socket的InputStream，并从中读取客户端发过来的信息了。
            Reader reader = new InputStreamReader(socket.getInputStream());
            char chars[] = new char[64];
            int len;
            StringBuilder sb = new StringBuilder();
            String temp;
            int index;
            while((len = reader.read(chars)) != -1) {
                temp = new String(chars, 0, len);
                if((index = temp.indexOf("eof")) != -1) {//遇到eof时就结束接收
                    sb.append(temp.substring(0, index));
                    break;
                }
                sb.append(temp);
            }
            System.out.println("from client: " + sb);
            //读完后写一句
            Writer writer = new OutputStreamWriter(socket.getOutputStream());
            writer.write("Hello Client.");
            writer.flush();
            writer.close();
            reader.close();
            socket.close();
        }
    }
}
