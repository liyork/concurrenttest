package com.wolf.concurrenttest.io.bio.basetest.buffer;

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
public class MultiThreadHandleServerBufferTest {

    public static void main(String args[]) throws IOException {
        int port = 8899;
        ServerSocket server = new ServerSocket(port);
        while(true) {
            Socket socket = server.accept();
            //每接收到一个Socket就建立一个新的线程来处理它
            new Thread(new Task(socket)).start();
        }
    }

    /**
     * 用来处理Socket请求的
     */
    static class Task implements Runnable {

        private Socket socket;

        public Task(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                handleSocket();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * 跟客户端Socket进行通信
         *
         * @throws Exception
         */
        private void handleSocket() throws Exception {
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
            StringBuilder sb = new StringBuilder();
            String temp;
            int index;
            while((temp = br.readLine()) != null) {//会卡住，当碰到\n或者流的结束符,认为读了一行
                System.out.println(temp);
                if((index = temp.indexOf("eof")) != -1) {//遇到eof时就结束接收
                    sb.append(temp.substring(0, index));
                    break;
                }
                sb.append(temp);
            }
            System.out.println("from client: " + sb);
            //读完后写一句
            Writer writer = new OutputStreamWriter(socket.getOutputStream(),"UTF-8");
            writer.write("Hello Client.");
            writer.write("eof\n");
            writer.flush();
            writer.close();
            br.close();
            socket.close();
        }
    }
}
