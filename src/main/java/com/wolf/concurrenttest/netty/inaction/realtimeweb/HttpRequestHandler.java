package com.wolf.concurrenttest.netty.inaction.realtimeweb;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedNioFile;

import java.io.RandomAccessFile;
import java.net.URL;

/**
 * Description: 处理http请求
 * <br/> Created on 9/26/17 7:30 PM
 *
 * @author 李超
 * @since 1.0.0
 */
public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private final String wsUri;

    public HttpRequestHandler(String wsUri) {
        this.wsUri = wsUri;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        if (wsUri.equalsIgnoreCase(request.uri())) {
            // Increases the reference count by 1, as after channelRead0() completes it will call release() on the FullHttpRequest and so release the resources of it.
            ctx.fireChannelRead(request.retain());// forward it to next ChannelInboundHandler
        } else {
            if (HttpUtil.is100ContinueExpected(request)) {// handle 100 continue request to conform HTTP 1.1
                send100Continue(ctx);
            }

            URL resource = HttpRequestHandler.class.getClassLoader().getResource("index.html");
            RandomAccessFile file = new RandomAccessFile(resource.getFile(), "r");
            // use a HttpRequest and not a FullHttpRequest as it is only the first part of the request
            HttpResponse response = new DefaultHttpResponse(request.protocolVersion(), HttpResponseStatus.OK);
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");// 告诉页面展示成html
            boolean keepAlive = HttpUtil.isKeepAlive(request);
            if (keepAlive) {
                response.headers().set(HttpHeaderNames.CONTENT_LENGTH, file.length());
                response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            }
            ctx.write(response);// not use writeAndFlush() as this should be done later

            if (ctx.pipeline().get(SslHandler.class) == null) {// zero-copy when no encryption / compression
                System.out.println("sslHandler is null");
                ctx.write(new DefaultFileRegion(file.getChannel(), 0, file.length()));
            } else {
                ctx.write(new ChunkedNioFile(file.getChannel()));
            }
            // all previous written messages will also be flushed out to the remote peer.
            ChannelFuture future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);// write and flush the LastHttpContent to the client which marks the request as complete, mark the end of the response and terminate it
            if (!keepAlive) {
                future.addListener(ChannelFutureListener.CLOSE);
            }
        }
    }

    private static void send100Continue(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE);
        ctx.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}