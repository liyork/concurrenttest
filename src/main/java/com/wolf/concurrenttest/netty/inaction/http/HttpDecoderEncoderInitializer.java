package com.wolf.concurrenttest.netty.inaction.http;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

/**
 * Description: 介绍http加解密类
 * <br/> Created on 9/24/17 1:29 AM
 *
 * @author 李超
 * @since 1.0.0
 */
public class HttpDecoderEncoderInitializer extends ChannelInitializer<Channel> {
    private final boolean client;

    public HttpDecoderEncoderInitializer(boolean client) {
        this.client = client;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        if (client) {//客户端，发送加密，接收解密
            pipeline.addLast("decoder", new HttpResponseDecoder());// decode receive response
            pipeline.addLast("encoder", new HttpRequestEncoder());// encode send request
        } else {
            pipeline.addLast("decoder", new HttpRequestDecoder());// decode http request
            pipeline.addLast("encoder", new HttpResponseEncoder());// encode http response
        }
    }
}
