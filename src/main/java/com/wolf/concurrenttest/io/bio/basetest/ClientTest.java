package com.wolf.concurrenttest.io.bio.basetest;

import java.io.*;
import java.net.Socket;

/**
 * Description:似乎一次连接只能写一次？然后flush，后续再写不行了？后期研究一下
 * <br/> Created on 2017/5/9 13:56
 *
 * @author 李超
 * @since 1.0.0
 */
public class ClientTest {

    static String host = "127.0.0.1";  //要连接的服务端IP地址
    static int port = 8899;   //要连接的服务端对应的监听端口

    public static void main(String args[]) throws Exception {

//        writeAndRead(client);

        //测试客户端由于网络等原因，很慢发送给服务端
        for (int i = 0; i < 10; i++) {
            writeAndReadSlow();
        }
    }

    private static void writeAndRead() throws IOException {
        Socket client = new Socket(host, port);
        Writer writer = new OutputStreamWriter(client.getOutputStream());
        writer.write("Hello Server.");
        writer.write("eof");
        writer.flush();//写完后要记得flush

        StringBuilder sb = new StringBuilder();
        char chars[] = new char[64];
        int len;
        System.out.println("11");
        Reader reader = new InputStreamReader(client.getInputStream());
        //会一直卡住
//        while ((len=reader.read(chars)) != -1) {
//            sb.append(new String(chars, 0, len));
//        }
        String temp;
        int index;
        while ((len=reader.read(chars)) != -1) {
            temp = new String(chars, 0, len);
            if ((index = temp.indexOf("eof")) != -1) {
                sb.append(temp.substring(0, index));
                break;
            }
            sb.append(new String(chars, 0, len));
        }
        System.out.println("from server: " + sb);

        System.out.println("22");
        writer.close();
        client.close();
    }

    //测试客户端由于网络原因写入很慢，会不会影响服务器的效率
    private static void writeAndReadSlow() throws IOException, InterruptedException {
        Socket client = new Socket(host, port);
        Writer writer = new OutputStreamWriter(client.getOutputStream());
        writer.write("Hello Server.");
        Thread.sleep(5000);
        writer.write("eof");
        writer.flush();//写完后要记得flush

        StringBuilder sb = new StringBuilder();
        char chars[] = new char[64];
        int len;
        System.out.println("11");

        Thread.sleep(4000);

        writer.close();
        client.close();
    }
}
