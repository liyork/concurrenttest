package com.wolf.concurrenttest.netty.jjzl.memory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Description: 内存未释放问题排查。
 * Created on 2021/5/30 6:18 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class MemoryServerHandler extends ChannelInboundHandlerAdapter {
    static ExecutorService executorService = Executors.newSingleThreadExecutor();
    PooledByteBufAllocator allocator = new PooledByteBufAllocator(false);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf reqMsg = (ByteBuf) msg;// 接收netty来的内存
        byte[] body = new byte[reqMsg.readableBytes()];
        executorService.execute(() -> {
            ByteBuf respMsg = allocator.heapBuffer(body.length);// 主动分配堆内存作为响应
            respMsg.writeBytes(body);
            ctx.writeAndFlush(respMsg);
        });
        // 需要主动释放读取的buf
        //ReferenceCountUtil.release(reqMsg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
