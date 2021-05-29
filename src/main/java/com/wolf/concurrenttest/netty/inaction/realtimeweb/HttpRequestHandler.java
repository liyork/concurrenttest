package com.wolf.concurrenttest.netty.inaction.realtimeweb;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedNioFile;

import java.io.RandomAccessFile;

/**
 * Description:
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
    public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest
            request) throws Exception {
        if (wsUri.equalsIgnoreCase(request.getUri())) {
            ctx.fireChannelRead(request.retain());

        }else {
            if (HttpHeaders.is100ContinueExpected(request)) {
                send100Continue(ctx);
            }

            String path = "/Users/chaoli/IdeaProjects/violin/violin-test/src/main/resources/index.jsp";

            RandomAccessFile file = new
                    RandomAccessFile(path, "r");
            HttpResponse response = new DefaultHttpResponse(
                    request.getProtocolVersion(), HttpResponseStatus.OK);
            response.headers().set(
                    HttpHeaders.Names.CONTENT_TYPE,
                    "text/plain; charset=UTF-8");
            boolean keepAlive = HttpHeaders.isKeepAlive(request);
            if (keepAlive) {
                response.headers().set(
                        HttpHeaders.Names.CONTENT_LENGTH, file.length());
                response.headers().set(
                        HttpHeaders.Names.CONNECTION,
                        HttpHeaders.Values.KEEP_ALIVE);
            }
            ctx.write(response);
            if (ctx.pipeline().get(SslHandler.class) == null) {//zero-copy when no encryption / compression
                System.out.println("sslHandler is null");
                ctx.write(new DefaultFileRegion(
                        file.getChannel(), 0, file.length()));
            } else {
                ctx.write(new ChunkedNioFile(file.getChannel()));
            }
            // all previous written messages will also be flushed out to the remote peer.
            ChannelFuture future = ctx.writeAndFlush(
                    LastHttpContent.EMPTY_LAST_CONTENT);
            if (!keepAlive) {
                future.addListener(ChannelFutureListener.CLOSE);
            }
        }
    }

    private static void send100Continue(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE);
        ctx.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}