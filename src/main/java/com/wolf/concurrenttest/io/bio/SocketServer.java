package com.wolf.concurrenttest.io.bio;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * <p> Description:同步方式处理
 * 服务器启动先监听(卡住)，有链接进来，打印客户传送的信息，从控制台读取信息(卡住)，不为bye则进入下面循环
 * 给客户发送刚才输入的信息，读取客户发送来的信息(卡住)，读取控制台信息(卡住)
 * <p/>
 * Date: 2016/6/20
 * Time: 16:49
 *
 * @author 李超
 * @version 1.0
 * @since 1.0
 */
public class SocketServer {

	public static void main(String args[]) {

		try {
			ServerSocket server = null;
			try {
				//创建一个ServerSocket在端口4700监听客户请求
				server = new ServerSocket(4700);
			} catch (Exception e) {
				//出错，打印出错信息
				System.out.println("can not listen to:" + e);
			}

			Socket socket = null;

			try {
				//使用accept()阻塞等待客户请求，有客户
				//请求到来则产生一个Socket对象，并继续执行
				socket = server.accept();
			} catch (Exception e) {
				//出错，打印出错信息
				System.out.println("Error." + e);
			}

			//由Socket对象得到输入流，并构造相应的BufferedReader对象
			InputStream inputStream = socket.getInputStream();
//			inputStream.read()//return:the next byte of data, or -1 if the end of the stream is reached.
			BufferedReader clientBr = new BufferedReader(new InputStreamReader(inputStream));
			//在标准输出上打印从客户端读入的字符串
			System.out.println("Client:" + clientBr.readLine());

			//由Socket对象得到输出流，并构造PrintWriter对象
			final PrintWriter clientPw = new PrintWriter(socket.getOutputStream());

			//由系统标准输入设备构造BufferedReader对象
			BufferedReader brFromSystemIn = new BufferedReader(new InputStreamReader(System.in));
			//从标准输入读入一字符串
			String line = brFromSystemIn.readLine();

			//如果该字符串为 "bye"，则停止循环
			while (!line.equals("bye")) {

				//向客户端输出该字符串
				clientPw.println(line);
				//刷新输出流，使Client马上收到该字符串
				clientPw.flush();
				//在系统标准输出上打印读入的字符串
				System.out.println("Server:" + line);

				//这里也可以定时发送心跳到客户端，如果没有回应则断开连接
//				ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
//				executorService.scheduleWithFixedDelay(new Runnable() {
//					@Override
//					public void run() {
//
//						clientPw.println("test...");
//					}
//				},2, 20,TimeUnit.SECONDS);


				//从Client读入一字符串，并打印到标准输出上
				System.out.println("Client:" + clientBr.readLine());
				//从系统标准输入读入一字符串
				line = brFromSystemIn.readLine();
			}

			clientPw.close(); //关闭Socket输出流

			clientBr.close(); //关闭Socket输入流

			socket.close(); //关闭Socket

			server.close(); //关闭ServerSocket

		} catch (Exception e) {
			e.printStackTrace();
			//出错，打印出错信息
			//System.out.println("Error:" + e);
		}

	}
}
