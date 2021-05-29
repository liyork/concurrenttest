package com.wolf.concurrenttest.io.bio.basetest.timeout;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Description:
 * <br/> Created on 2017/5/9 13:56
 *
 * @author 李超
 * @since 1.0.0
 */
public class ClientTimeoutTest {

    public static void main(String args[]) throws Exception {
        String host = "127.0.0.1";  //要连接的服务端IP地址
        int port = 8899;   //要连接的服务端对应的监听端口
        Socket client = new Socket(host, port);
        Writer writer = new OutputStreamWriter(client.getOutputStream());
        writer.write("Hello Server.");
        writer.write("eof\n");
        writer.flush();

        BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
        //设置超时间为10秒
        client.setSoTimeout(3 * 1000);//设置read的超时时间
        StringBuffer sb = new StringBuffer();
        String temp;
        int index;
        try {
            System.out.println("1111");
            while((temp = br.readLine()) != null) {//卡住
                System.out.println("222");
                if((index = temp.indexOf("eof")) != -1) {
                    sb.append(temp.substring(0, index));
                    break;
                }
                sb.append(temp);
            }
        } catch (SocketTimeoutException e) {
            System.out.println("数据读取超时。");
            e.printStackTrace();
        }
        System.out.println("from server: " + sb);
        writer.close();
        br.close();
        client.close();
    }
}
