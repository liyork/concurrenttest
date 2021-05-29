package com.wolf.concurrenttest.netty.two;

import org.jboss.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p> Description:
 * <p/>
 * Date: 2016/7/13
 * Time: 9:37
 *
 * @author 李超
 * @version 1.0
 * @since 1.0
 */
@Deprecated //基于netty-3.7.0.Final.jar 废弃了，都用4了， 用inaction包的
public class MessageClientHandler extends SimpleChannelUpstreamHandler {

	Logger logger = LoggerFactory.getLogger(MessageClientHandler.class);

	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
		e.getChannel().write(getAString(256));
	}

	@Override
	public void messageReceived(
			ChannelHandlerContext ctx, MessageEvent e) {
		// Send back the received message to the remote peer.
		System.out.println("messageReceived send message " + e.getMessage());
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		e.getChannel().write(e.getMessage());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
		// Close the connection when an exception is raised.
		logger.error("Unexpected exception from downstream.", e.getCause());
		e.getChannel().close();
	}

	public String getAString(int size) {
		StringBuilder str = new StringBuilder("");
		for (int i = 0; i < size; i++) {
			str.append("a");
		}
		return str.toString();
	}
}
