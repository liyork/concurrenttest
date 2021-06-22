package com.wolf.concurrenttest.netty.inaction.realtimeweb;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * Description: 初始化handler
 * <br/> Created on 9/27/17 9:11 AM
 *
 * @author 李超
 * @since 1.0.0
 */
public class ChatServerInitializer extends ChannelInitializer<Channel> {
    private final ChannelGroup group;

    public ChatServerInitializer(ChannelGroup group) {
        this.group = group;
    }

    // most of the times, this method setup the ChannelPipeline of the newly registered Channel.
    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new HttpServerCodec());// decode bytes to HTTP request/encode HTTP requests to bytes
        pipeline.addLast(new ChunkedWriteHandler());// allows to write a file content
        // aggregate decoded HttpRequest/HttpContent/LastHttpContent to FullHttpRequest.
        pipeline.addLast(new HttpObjectAggregator(64 * 1024));
        pipeline.addLast(new HttpRequestHandler("/ws"));// handle FullHttpRequest which are not send to /ws URI and so serve the index.html page
        // handle the WebSocket upgrade and Ping/Pong/Close WebSocket frames to be RFC compliant. also handle the handshake.
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
        pipeline.addLast(new TextWebSocketFrameHandler(group));// handles Text frames and handshake completion events
    }
}
