package com.wolf.concurrenttest.netty.one;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

/**
 * <p> Description:
 * <p/>
 * Date: 2016/7/11
 * Time: 16:02
 *
 * @author 李超
 * @version 1.0
 * @since 1.0
 */
@Deprecated //基于netty-3.7.0.Final.jar 废弃了，都用4了， 用inaction包的
public class ObjectClientHandler  extends SimpleChannelHandler {

	/**
	 * 当绑定到服务端的时候触发，给服务端发消息。
	 *
	 * @author lihzh
	 * @alia OneCoder
	 */
	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
		// 向服务端发送Object信息
		sendObject(e.getChannel());
	}

	/**
	 * 发送Object
	 *
	 * @param channel
	 * @author lihzh
	 * @alia OneCoder
	 */
	private void sendObject(Channel channel) {
		Command command =new Command();
		command.setActionName("Hello action.");
		channel.write(command);
	}
}
