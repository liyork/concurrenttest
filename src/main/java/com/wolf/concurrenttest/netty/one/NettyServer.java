package com.wolf.concurrenttest.netty.one;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * <p> Description:
 * <p/>
 * Date: 2016/7/11
 * Time: 15:35
 *
 * @author 李超
 * @version 1.0
 * @since 1.0
 */
@Deprecated //基于netty-3.7.0.Final.jar 废弃了，都用4了， 用inaction包的
public class NettyServer {

	public static void main(String args[]) {
		// Server服务启动器
		ServerBootstrap bootstrap = new ServerBootstrap(
				new NioServerSocketChannelFactory(
						Executors.newCachedThreadPool(),
						Executors.newCachedThreadPool()));
		// 设置一个处理客户端消息和各种消息事件的类(Handler)
		//简单healloworld
//		bootstrap.setPipelineFactory(getSimplePipelineFactory());
		//传输对象
		bootstrap.setPipelineFactory(getPipelineTransformObjectFactory());
		// 开放8000端口供客户端访问。
		bootstrap.bind(new InetSocketAddress(8000));
	}

	private static ChannelPipelineFactory getPipelineTransformObjectFactory() {
		//基于3.7.0先暂时注掉
//		return new ChannelPipelineFactory() {
//			@Override
//			public ChannelPipeline getPipeline()throws Exception {
//				//需要顺序,write时，从tail-->head，是down过程。read时，从head-->tail，是up过程
//				return Channels.pipeline(
//						new ObjectDecoder(ClassResolvers.cacheDisabled(this.getClass().getClassLoader())),
//						new ObjectServerHandler());
//			}
//		};
		return null;
	}

	private static ChannelPipelineFactory getSimplePipelineFactory() {
		return new ChannelPipelineFactory() {
			@Override
			public ChannelPipeline getPipeline()
					throws Exception {
				return Channels.pipeline(new HelloServerHandler());
			}
		};
	}

	private static class HelloServerHandler extends SimpleChannelHandler {

		/**
		 * 当有客户端绑定到服务端的时候触发，打印"Hello world, I'm server."
		 *
		 * @alia OneCoder
		 */
		@Override
		public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
			System.out.println("Hello world, I'm server.");
		}
	}
}
