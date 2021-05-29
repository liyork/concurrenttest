package com.wolf.concurrenttest.netty.inaction.http;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * Description:压缩
 * <br/> Created on 9/24/17 2:46 AM
 *
 * @author 李超
 * @since 1.0.0
 */
public class HttpDecompressorInitializer extends ChannelInitializer<Channel> {
    private final boolean client;

    public HttpDecompressorInitializer(boolean client) {
        this.client = client;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        if (client) {
            pipeline.addLast("codec", new HttpClientCodec());
        } else {
            pipeline.addLast("codec", new HttpServerCodec());
        }
        pipeline.addLast("decompressor", new HttpContentDecompressor());
    }
}
