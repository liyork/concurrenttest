package com.wolf.concurrenttest.netty.two;

import org.jboss.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p> Description:
 * <p/>
 * Date: 2016/7/13
 * Time: 9:44
 *
 * @author 李超
 * @version 1.0
 * @since 1.0
 */
@Deprecated //基于netty-3.7.0.Final.jar 废弃了，都用4了， 用inaction包的
public class MessageServerHandler2 extends SimpleChannelUpstreamHandler {

	private Logger logger = LoggerFactory.getLogger(MessageClientHandler.class);

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
		System.out.println("22222");
		e.getChannel().write(e.getMessage());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
		logger.error("Unexpected exception from downstream.",e.getCause());
		e.getChannel().close();
	}

	@Override
	public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e)throws Exception {
	}

	@Override
	public void channelConnected(ChannelHandlerContext ctx,ChannelStateEvent e) throws Exception {
	}

}
