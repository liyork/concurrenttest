package com.wolf.concurrenttest.io.bio.heartbeat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * <p> Description:测试读超时
 * <p/>
 * Date: 2016/6/20
 * Time: 16:48
 *
 * @author 李超
 * @version 1.0
 * @since 1.0
 */
public class TestHeartBeatClient {

	public static void main(String args[]) {
		try {

			//向本机的4700端口发出客户请求
			Socket socket = new Socket("127.0.0.1", 4700);
			//由Socket对象得到输出流，并构造PrintWriter对象
			PrintWriter pwToServer = new PrintWriter(socket.getOutputStream());

			//由系统标准输入设备构造BufferedReader对象
			BufferedReader brFromSystemIn = new BufferedReader(new InputStreamReader(System.in));
			String readLine = brFromSystemIn.readLine(); //从系统标准输入读入一字符串

			//若从标准输入读入的字符串为 "bye"则停止循环
			while (!readLine.equals("bye")) {
				//将从系统标准输入读入的字符串输出到Server
				pwToServer.println(readLine);
				//刷新输出流，使Server马上收到该字符串
				pwToServer.flush();
				//在系统标准输出上打印读入的字符串
				System.out.println("Client:" + readLine);

				readLine = brFromSystemIn.readLine(); //从系统标准输入读入一字符串

			}

			pwToServer.close(); //关闭Socket输出流

			socket.close(); //关闭Socket

		} catch (Exception e) {
			System.out.println("Error" + e); //出错，则打印出错信息

		}
	}
}
