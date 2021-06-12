
package com.wolf.concurrenttest.jjzl.trigger.ssl;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslCloseCompletionEvent;
import io.netty.handler.ssl.SslHandshakeCompletionEvent;

/**
 * 监听ssl事件，todo 如何进行有效控制?
 */
@ChannelHandler.Sharable
public class SslServerHandler extends SimpleChannelInboundHandler<Object> {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        // discard
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt == SslHandshakeCompletionEvent.SUCCESS) {
            // 这里可以执行流控
            SslServer.channelMap.put(ctx.channel().id().toString(), (NioSocketChannel) ctx.channel());
        } else if (evt == SslCloseCompletionEvent.SUCCESS) {
            SslServer.channelMap.remove(ctx.channel().id().toString());
        }
    }
}
