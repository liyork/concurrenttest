package com.wolf.concurrenttest.netty.two;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

/**
 * <p> Description:
 * <p/>
 * Date: 2016/7/13
 * Time: 9:41
 *
 * @author 李超
 * @version 1.0
 * @since 1.0
 */
@Deprecated //基于netty-3.7.0.Final.jar 废弃了，都用4了， 用inaction包的
public class MessageEncoder  extends OneToOneEncoder {

	@Override
	protected Object encode(ChannelHandlerContext ctx, Channel channel,
							Object msg) throws Exception {

		if (!(msg instanceof String)) {
			return msg;//(1)
		}
		String res = (String)msg;
		byte[] data = res.getBytes();
		int dataLength = data.length;
		ChannelBuffer buf = ChannelBuffers.dynamicBuffer();//(2)
		buf.writeInt(dataLength);
		buf.writeBytes(data);
		return buf;
	}
}
