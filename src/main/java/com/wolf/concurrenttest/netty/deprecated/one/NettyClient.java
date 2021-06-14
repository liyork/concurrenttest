package com.wolf.concurrenttest.netty.deprecated.one;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.serialization.ObjectEncoder;

import java.net.InetSocketAddress;

import static java.util.concurrent.Executors.newCachedThreadPool;

/**
 * <p> Description:bootstrap.bind时触发所有的handler的会触发ObjectClientHandler的channelConnected方法，进而里面write会触发它前面的
 * handler即ObjectEncoder
 * <p/>
 * Date: 2016/7/11
 * Time: 15:40
 *
 * @author 李超
 * @version 1.0
 * @since 1.0
 */
@Deprecated //基于netty-3.7.0.Final.jar 废弃了，都用4了， 用inaction包的
public class NettyClient {

	public static void main(String args[]) {
		// Client服务启动器
		ClientBootstrap bootstrap = new ClientBootstrap(
				new NioClientSocketChannelFactory(
						newCachedThreadPool(),
						newCachedThreadPool()));
		// 设置一个处理服务端消息和各种消息事件的类(Handler)
		//简单healloworld
//		bootstrap.setPipelineFactory(getSimplePipelineFactory());
		//传输对象
		bootstrap.setPipelineFactory(getPipelineTransformObjectFactory());
		// 连接到本地的8000端口的服务端
		bootstrap.connect(new InetSocketAddress("127.0.0.1", 8000));
	}

	private static ChannelPipelineFactory getPipelineTransformObjectFactory() {
		return new ChannelPipelineFactory() {
			@Override
			public ChannelPipeline getPipeline()throws Exception {
				//需要顺序
				return Channels.pipeline(new ObjectEncoder(),
						new ObjectClientHandler());
			}
		};
	}

	private static ChannelPipelineFactory getSimplePipelineFactory() {
		return new ChannelPipelineFactory() {
			@Override
			public ChannelPipeline getPipeline() throws Exception {
				return Channels.pipeline(new HelloClientHandler());
			}
		};
	}

	private static class HelloClientHandler extends SimpleChannelHandler {


		/**
		 * 当绑定到服务端的时候触发，打印"Hello world, I'm client."
		 *
		 * @alia OneCoder
		 */
		@Override
		public void channelConnected(ChannelHandlerContext ctx,
									 ChannelStateEvent e) {
			System.out.println("Hello world, I'm client.");
		}
	}
}
