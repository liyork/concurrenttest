package com.wolf.concurrenttest.io.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * <p> Description:
 * <p/>
 * Date: 2015/10/23
 * Time: 13:36
 *
 * @author 李超
 * @version 1.0
 * @since 1.0
 */
public class TCPClient{
	// 信道选择器
	private Selector selector;

	// 与服务器通信的信道
	SocketChannel socketChannel;

	// 要连接的服务器Ip地址
	private String hostIp;

	// 要连接的远程服务器在监听的端口
	private int hostListenningPort;

	/**
	 * 构造函数
	 * @param HostIp
	 * @param HostListenningPort
	 * @throws java.io.IOException
	 */
	public TCPClient(String HostIp,int HostListenningPort)throws IOException{
		this.hostIp=HostIp;
		this.hostListenningPort=HostListenningPort;

		initialize();
	}

	/**
	 * 初始化
	 * @throws java.io.IOException
	 */
	private void initialize() throws IOException{
		// 打开监听信道并设置为非阻塞模式
		//触发服务端的accept
		InetSocketAddress remote = new InetSocketAddress(hostIp, hostListenningPort);
		socketChannel= SocketChannel.open();
		socketChannel.connect(remote);
		//一定要先链接后设定Blocking=false
		socketChannel.configureBlocking(false);

		// 打开并注册选择器到信道
		selector = Selector.open();
		socketChannel.register(selector, SelectionKey.OP_READ);

		// 启动读取线程
		new TCPClientReadThread(selector,socketChannel);

	}

	/**
	 * 发送字符串到服务器
	 * @param message
	 * @throws java.io.IOException
	 */
	public void sendMsg(String message) throws IOException {
		ByteBuffer writeBuffer= ByteBuffer.wrap(message.getBytes("UTF-8"));
		while(writeBuffer.hasRemaining()) {
			socketChannel.write(writeBuffer);
		}
	}

	public static void main(String[] args) throws IOException{
		TCPClient client=new TCPClient("127.0.0.1",1978);

		client.sendMsg("你好!Nio!醉里挑灯看剑,梦回吹角连营1");
		client.sendMsg("你好!Nio!醉里挑灯看剑,梦回吹角连营2");
	}
}
