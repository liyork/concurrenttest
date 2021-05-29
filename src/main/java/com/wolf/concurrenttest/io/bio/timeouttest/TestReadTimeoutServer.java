package com.wolf.concurrenttest.io.bio.timeouttest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 * <p> Description:测试读超时
 * <p/>
 * Date: 2016/6/20
 * Time: 16:49
 *
 * @author 李超
 * @version 1.0
 * @since 1.0
 */
public class TestReadTimeoutServer {

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

			//4秒超时
			socket.setSoTimeout(4000);

			//由Socket对象得到输入流，并构造相应的BufferedReader对象
			BufferedReader clientBr = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			//在标准输出上打印从客户端读入的字符串
			try {
				System.out.println("Client:" + clientBr.readLine());
			} catch (SocketTimeoutException e) {
				e.printStackTrace();

				System.out.println(socket.isBound()); // 是否邦定
				System.out.println(socket.isClosed()); // 是否关闭
				System.out.println(socket.isConnected()); // 是否连接
				System.out.println(socket.isInputShutdown()); // 是否关闭输入流
				System.out.println(socket.isOutputShutdown()); // 是否关闭输出流
			} catch (SocketException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

			clientBr.close(); //关闭Socket输入流

		} catch (Exception e) {
			//出错，打印出错信息
			System.out.println("Error:" + e);
		}

	}
}
