package com.wolf.concurrenttest.io.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.*;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p> Description:
 * <p/>
 * Date: 2015/10/23
 * Time: 13:37
 *
 * @author 李超
 * @version 1.0
 * @since 1.0
 */
public class TCPProtocolImpl implements TCPProtocol{
	private int bufferSize;
	private AtomicInteger atomicInteger;

	public TCPProtocolImpl(int bufferSize,AtomicInteger atomicInteger){
		this.bufferSize=bufferSize;
		this.atomicInteger=atomicInteger;
	}

	public void handleAccept(SelectionKey key) throws IOException {
		SocketChannel clientChannel=((ServerSocketChannel)key.channel()).accept();
		if (clientChannel == null) {
			throw new RuntimeException("不能为空");
		}
		clientChannel.configureBlocking(false);
		clientChannel.register(key.selector(), SelectionKey.OP_READ,ByteBuffer.allocate(bufferSize));
	}

	public void   handleRead(SelectionKey key) throws IOException {
		// 获得与客户端通信的信道
		SocketChannel clientChannel=(SocketChannel)key.channel();

		// 得到并清空缓冲区
		ByteBuffer buffer=(ByteBuffer)key.attachment();
		buffer.clear();

//		(如果一次读取的比client写的少，还会触发select继续读)
//		ByteBuffer buffer = ByteBuffer.allocate(12);

		long bytesRead;
		try{
			// 读取信息获得读取的字节数
			bytesRead=clientChannel.read(buffer);
		}catch(IOException e){//防止客户端异常关闭
			cancelAndClose(key, clientChannel);
			return;
		}

		//客户端会调用两次读，这里处理一下，解除客户端channel的注册
		// (第一次正常读，第二次是-1,-1说明客户端的数据发送完毕，并且主动调用socketChannel.close())
		if (bytesRead == -1) {
			cancelAndClose(key, clientChannel);
		}else{
			// 准备读取缓冲区
			buffer.flip();

			// 将字节转化为为UTF-8的字符串
			Charset charset = Charset.forName("UTF-8");
			String receivedString= charset.newDecoder().decode(buffer).toString();

			// 控制台打印出来
			System.out.println("接收到来自__"+clientChannel.socket().getRemoteSocketAddress()+"__的信息__"+receivedString);

			// 准备发送的文本
			String sendString="你好,客户端. @"+new Date().toString()+"，已经收到你的信息"+receivedString+"__"+atomicInteger.getAndIncrement();

			CharsetEncoder encoder = charset.newEncoder();
			CharBuffer writeBuffer = CharBuffer.allocate(1024);
			writeBuffer.put(sendString);
			writeBuffer.flip();
			ByteBuffer encode = encoder.encode(writeBuffer);
			//Write()方法无法保证能写多少字节到SocketChannel
			while(encode.hasRemaining()) {
				clientChannel.write(encode);
			}


			// 设置为下一次读取或是写入做准备
			//key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
		}

	}

	private void cancelAndClose(SelectionKey key, SocketChannel clientChannel) throws IOException {
		key.cancel();
		clientChannel.socket().close();
		clientChannel.close();
	}

	public void handleWrite(SelectionKey key) throws IOException {
		// do nothing
	}

}
