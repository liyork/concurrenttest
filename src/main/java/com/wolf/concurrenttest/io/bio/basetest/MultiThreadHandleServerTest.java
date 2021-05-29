package com.wolf.concurrenttest.io.bio.basetest;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Description:每个连接一个线程，线程创建也需要消耗很多资源
 * 每个请求都占用一个线程，线程也需要内存空间，多了也可能引起cpu切换，太多了影响机器性能
 * one connection one thread。无论连接是否有真正数据请求，都需要独占一个thread。
 *
 * 若客户端由于网络等原因发送数据很慢，那么服务端开启给这个客户端服务的线程将很长时间等待在客户端的io操作上。
 * 连接一多，那么线程就忙不过来了，不能分配新线程了。
 * <br/> Created on 2017/5/9 14:12
 *
 * @author 李超
 * @since 1.0.0
 */
public class MultiThreadHandleServerTest {

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
            Reader reader = new InputStreamReader(socket.getInputStream());
            char chars[] = new char[64];
            int len;
            StringBuilder sb = new StringBuilder();
            String temp;
            int index;
            long start = System.currentTimeMillis();
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

            System.out.println("total cost :"+(System.currentTimeMillis() - start));
            writer.flush();
            writer.close();
            reader.close();
            socket.close();
        }

    }
}
