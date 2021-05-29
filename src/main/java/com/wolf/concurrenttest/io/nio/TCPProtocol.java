package com.wolf.concurrenttest.io.nio;

import java.io.IOException;
import java.nio.channels.SelectionKey;

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
public interface TCPProtocol{
	/**
	 * 接收一个SocketChannel的处理
	 * @param key
	 * @throws java.io.IOException
	 */
	void handleAccept(SelectionKey key) throws IOException;

	/**
	 * 从一个SocketChannel读取信息的处理
	 * @param key
	 * @throws java.io.IOException
	 */
	void handleRead(SelectionKey key) throws IOException;

	/**
	 * 向一个SocketChannel写入信息的处理
	 * @param key
	 * @throws java.io.IOException
	 */
	void handleWrite(SelectionKey key) throws IOException;
}
