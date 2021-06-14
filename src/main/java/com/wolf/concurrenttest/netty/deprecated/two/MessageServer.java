package com.wolf.concurrenttest.netty.deprecated.two;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * <p> Description:
 * <p/>
 * Date: 2016/7/13
 * Time: 9:43
 *
 * @author 李超
 * @version 1.0
 * @since 1.0
 */
@Deprecated //基于netty-3.7.0.Final.jar 废弃了，都用4了， 用inaction包的
public class MessageServer {

	public static void main(String[] args) throws Exception {
		// Configure the server.
		ServerBootstrap bootstrap = new ServerBootstrap(
				new NioServerSocketChannelFactory(
						Executors.newCachedThreadPool(),
						Executors.newCachedThreadPool()));

		// Set up the default event pipeline.
		// bootstrap.setPipelineFactory(new MessageServerPipelineFactory());
		bootstrap.getPipeline().addLast("decoder", new MessageDecoder());
		bootstrap.getPipeline().addLast("encoder", new MessageEncoder());
		bootstrap.getPipeline().addLast("handler1", new MessageServerHandler());
		bootstrap.getPipeline().addLast("handler2", new MessageServerHandler2());
		// Bind and start to accept incoming connections.
		bootstrap.bind(new InetSocketAddress(9550));
	}
}
