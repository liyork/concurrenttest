package com.wolf.concurrenttest.netty.inaction.realtimeweb;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

/**
 * Description: 处理webSocket的TextWebSocketFrame请求
 * <br/> Created on 9/27/17 8:50 AM
 *
 * @author 李超
 * @since 1.0.0
 */
public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    private final ChannelGroup group;

    public TextWebSocketFrameHandler(ChannelGroup group) {
        this.group = group;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt == WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE) {
            ctx.pipeline().remove(HttpRequestHandler.class);// 优化

            group.writeAndFlush(new TextWebSocketFrame("Client " + ctx.channel() + " joined"));//向所有channel中写入数据
            group.add(ctx.channel());// 添加本channel
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        //向group中的所有channel发送收到的消息
        group.writeAndFlush(msg.retain());
    }
}
