package com.wolf.concurrenttest.io.bio.basetest.buffer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;

/**
 * Description:
 * <br/> Created on 2017/5/9 13:56
 *
 * @author 李超
 * @since 1.0.0
 */
public class ClientBufferTest {

    public static void main(String args[]) throws Exception {
        String host = "127.0.0.1";  //要连接的服务端IP地址
        int port = 8899;   //要连接的服务端对应的监听端口
        Socket client = new Socket(host, port);
        Writer writer = new OutputStreamWriter(client.getOutputStream(),"UTF-8");
        writer.write("Hello Server.");
        writer.write("eof\n");//服务端使用readLine，这里需要手动添加\n，代表Hello Server.eof是一行
        writer.flush();

        BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream(),"UTF-8"));
        StringBuffer sb = new StringBuffer();
        String temp;
        int index;
        while((temp = br.readLine()) != null) {
            if((index = temp.indexOf("eof")) != -1) {
                sb.append(temp.substring(0, index));
                break;
            }
            sb.append(temp);
        }
        System.out.println("from server: " + sb);
        writer.close();
        br.close();
        client.close();
    }
}
