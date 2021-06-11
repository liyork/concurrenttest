package com.wolf.concurrenttest.jjzl.gateway;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Description: 按需分配数组
 * Created on 2021/6/1 9:17 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ApiGatewayServerHandler2 extends ChannelInboundHandlerAdapter {
    ExecutorService executorService = Executors.newFixedThreadPool(18);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ctx.write(msg);
        char[] req = new char[((ByteBuf) msg).readableBytes()];
        executorService.execute(() -> {
            char[] dispatchReq = req;
            // 模拟业务处理
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
