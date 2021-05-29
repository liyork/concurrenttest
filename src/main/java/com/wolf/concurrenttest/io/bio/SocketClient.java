package com.wolf.concurrenttest.io.bio;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Socket是应用层与TCP/IP协议族通信的中间软件抽象层，它是一组接口
 * <p> Description:同步方式处理
 * 与服务器建立链接，从控制台获取信息(卡住)，不为bye则进入下面循环
 * 发送刚才控制台数据，从服务器读取数据(卡住)，从控制台读取数据(卡住)
 *
 * socket是"打开—读/写—关闭"模式的实现
 * <p/>
 * Date: 2016/6/20
 * Time: 16:48
 *
 * @author 李超
 * @version 1.0
 * @since 1.0
 */
public class SocketClient {

    public static void main(String args[]) {

        try {
            //连接时间超时connectionTimeout
            int connectionTimeout = 2000;
            //向本机的4700端口发出客户请求
            Socket socket = new Socket();

            socket.connect(new InetSocketAddress("127.0.0.1", 4700), connectionTimeout);
            System.out.println("socket connect ...");
            //读取数据超时soTimeout
            //socket.setSoTimeout(4000);//4s若未从服务端读取内容则主动关闭连接

            //由Socket对象得到输出流，并构造PrintWriter对象
            PrintWriter pwToServer = new PrintWriter(socket.getOutputStream());
            //由Socket对象得到输入流，并构造相应的BufferedReader对象
            BufferedReader brToServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            //由系统标准输入设备构造BufferedReader对象
            BufferedReader brFromSystemIn = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("prepare readline  ...");
            String readLine = brFromSystemIn.readLine(); //从系统标准输入读入一字符串
            System.out.println("after readline  ...");

            //若从标准输入读入的字符串为 "bye"则停止循环
            while(!readLine.equals("bye")) {
                //将从系统标准输入读入的字符串输出到Server
                pwToServer.println(readLine);
                //刷新输出流，使Server马上收到该字符串
                pwToServer.flush();
                //在系统标准输出上打印读入的字符串
                System.out.println("Client:" + readLine);
                //从Server读入一字符串，并打印到标准输出上
                System.out.println("Server:" + brToServer.readLine());

                readLine = brFromSystemIn.readLine(); //从系统标准输入读入一字符串

            }

            pwToServer.close(); //关闭Socket输出流

            brToServer.close(); //关闭Socket输入流

            socket.close(); //关闭Socket

        } catch (Exception e) {
            e.printStackTrace();
//            System.out.println("Error" + e); //出错，则打印出错信息

        }
    }
}
