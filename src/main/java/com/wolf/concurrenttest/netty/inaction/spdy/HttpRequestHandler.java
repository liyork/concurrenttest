package com.wolf.concurrenttest.netty.inaction.spdy;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

/**
 * Description:
 * <br/> Created on 9/27/17 8:11 PM
 *
 * @author 李超
 * @since 1.0.0
 */
public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    @Override
    public void channelRead0(ChannelHandlerContext ctx,
                             FullHttpRequest request) throws Exception {
        if (HttpHeaders.is100ContinueExpected(request)) {
            send100Continue(ctx);
        }

        FullHttpResponse response = new DefaultFullHttpResponse(
                request.getProtocolVersion(), HttpResponseStatus.OK);
        response.content().writeBytes(getContent()
                .getBytes(CharsetUtil.UTF_8));
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE,
                "text/plain; charset=UTF-8");
        boolean keepAlive = HttpHeaders.isKeepAlive(request);
        if (keepAlive) {
            response.headers().set(HttpHeaders.Names.CONTENT_LENGTH,
                    response.content().readableBytes());
            response.headers().set(HttpHeaders.Names.CONNECTION,
                    HttpHeaders.Values.KEEP_ALIVE);
        }
        ChannelFuture future = ctx.writeAndFlush(response);
        if (!keepAlive) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }

    protected String getContent() {
        return "This content is transmitted via HTTP\r\n";
    }

    private static void send100Continue(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(

                HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE);
        ctx.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,
                                Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}