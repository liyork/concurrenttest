package com.wolf.concurrenttest.io.bio.timeouttest;

import java.net.Socket;

/**
 * <p> Description:异常关闭
 * <p/>
 * Date: 2016/6/20
 * Time: 16:48
 *
 * @author 李超
 * @version 1.0
 * @since 1.0
 */
public class AbnormalCloseConnectClient {

	public static void main(String args[]) {
		try {
			//向本机的4700端口发出客户请求
			Socket socket = new Socket("127.0.0.1", 4700);

		} catch (Exception e) {
			System.out.println("Error" + e); //出错，则打印出错信息

		}
	}
}
