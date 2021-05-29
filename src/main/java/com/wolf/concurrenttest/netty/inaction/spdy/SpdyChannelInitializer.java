package com.wolf.concurrenttest.netty.inaction.spdy;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslHandler;
import org.eclipse.jetty.npn.NextProtoNego;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

/**
 * Description:
 * <br/> Created on 9/28/17 10:31 AM
 *
 * @author 李超
 * @since 1.0.0
 */
public class SpdyChannelInitializer extends
        ChannelInitializer<SocketChannel> {
    private final SSLContext context;

    public SpdyChannelInitializer(SSLContext context) {
        this.context = context;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        SSLEngine engine = context.createSSLEngine();
        engine.setUseClientMode(false);
        NextProtoNego.put(engine, new DefaultServerProvider());
        NextProtoNego.debug = true;
        pipeline.addLast("sslHandler", new SslHandler(engine));
//        需要
//                <dependency>
//            <groupId>io.netty</groupId>
//            <artifactId>netty-all</artifactId>
//            <version>4.0.0.Final</version>
//        </dependency>
//        pipeline.addLast("chooser",
//                new DefaultSpdyOrHttpChooser(1024 * 1024, 1024 * 1024));
    }
}
