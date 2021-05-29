package com.wolf.concurrenttest.netty.one;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

/**
 * <p> Description:
 * <p/>
 * Date: 2016/7/11
 * Time: 16:01
 *
 * @author 李超
 * @version 1.0
 * @since 1.0
 */
@Deprecated //基于netty-3.7.0.Final.jar 废弃了，都用4了， 用inaction包的
public class ObjectServerHandler extends SimpleChannelHandler {

	/**
	 * 当接受到消息的时候触发
	 */
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
	throws Exception {
		Command command = (Command) e.getMessage();
		// 打印看看是不是我们刚才传过来的那个
		System.out.println(command.getActionName());
	}
}
