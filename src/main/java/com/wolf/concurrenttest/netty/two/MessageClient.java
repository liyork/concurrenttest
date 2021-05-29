package com.wolf.concurrenttest.netty.two;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * <p> Description:
 * <p/>
 * Date: 2016/7/13
 * Time: 9:36
 *
 * @author 李超
 * @version 1.0
 * @since 1.0
 */
@Deprecated //基于netty-3.7.0.Final.jar 废弃了，都用4了， 用inaction包的
public class MessageClient {

	public static void main(String[] args) throws Exception {
		String host = "127.0.0.1";
		int port = 9550;
		// Configure the client.
		ClientBootstrap bootstrap = new ClientBootstrap(
				new NioClientSocketChannelFactory(
						Executors.newCachedThreadPool(),
						Executors.newCachedThreadPool()));
		// Set up the event pipeline factory.
		// bootstrap.setPipelineFactory(new MessageServerPipelineFactory());
		bootstrap.getPipeline().addLast("decoder", new MessageDecoder());
		bootstrap.getPipeline().addLast("encoder", new MessageEncoder());
		bootstrap.getPipeline().addLast("handler", new MessageClientHandler());
		// Start the connection attempt.
		ChannelFuture future = bootstrap.connect(new InetSocketAddress(host, port));
		// Wait until the connection is closed or the connection attempt fails.
		future.getChannel().getCloseFuture().awaitUninterruptibly();

		// Shut down thread pools to exit.
		//  future.getChannel().write("我们都是中国人 啊啊！");
		bootstrap.releaseExternalResources();
	}
}
