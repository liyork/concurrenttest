package com.wolf.concurrenttest.netty.jjzl.bytebuf;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.concurrent.DefaultPromise;

import java.util.concurrent.ExecutionException;

public class HttpClient {

    private Channel channel;
    HttpClientHandler handler = new HttpClientHandler();

    private void connect(String host, int port) throws Exception {
        EventLoopGroup workerGroup = new NioEventLoopGroup(1);
        Bootstrap b = new Bootstrap()
                .group(workerGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new HttpClientCodec())
                                .addLast(new HttpObjectAggregator(Short.MAX_VALUE))
                                .addLast(handler);
                    }
                });
        ChannelFuture f = b.connect(host, port).sync();
        channel = f.channel();
    }

    private HttpResponse blockSend(FullHttpRequest request) throws InterruptedException, ExecutionException {
        // 设定头长
        request.headers().set(HttpHeaderNames.CONTENT_LENGTH, request.content().readableBytes());
        DefaultPromise<HttpResponse> respPromise = new DefaultPromise<HttpResponse>(channel.eventLoop());
        handler.setRespPromise(respPromise);
        channel.writeAndFlush(request);
        // 阻塞获取响应
        HttpResponse response = respPromise.get();// 这里是main线程执行
        if (response != null)
            System.out.print("The client received http response, the body is :" + new String(response.body()));
        return response;
    }

    public static void main(String[] args) throws Exception {
        HttpClient client = new HttpClient();
        client.connect("127.0.0.1", 18084);
        ByteBuf body = Unpooled.wrappedBuffer("Http message!".getBytes("UTF-8"));
        DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET,
                "http://127.0.0.1/user?id=10&addr=xxx", body);
        client.blockSend(request);
    }
}
