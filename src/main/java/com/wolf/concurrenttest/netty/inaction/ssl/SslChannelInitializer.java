package com.wolf.concurrenttest.netty.inaction.ssl;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

/**
 * Description: 用netty提供的SslHandler进行安全处理
 * <br/> Created on 9/24/17 1:08 AM
 *
 * @author 李超
 * @since 1.0.0
 */
public class SslChannelInitializer extends ChannelInitializer<Channel> {

    private final SSLContext context;
    private final boolean client;
    private final boolean startTls;

    public SslChannelInitializer(SSLContext context, boolean client, boolean startTls) {
        this.context = context;
        this.client = client;
        this.startTls = startTls;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        SSLEngine engine = context.createSSLEngine();
        engine.setUseClientMode(client);// use client mode

        // in almost all cases the SslHandler must be the first ChannelHandler in the ChannelPipeline
        ch.pipeline().addFirst("ssl", new SslHandler(engine, startTls));
    }
}