package com.wolf.concurrenttest.netty.inaction.pipeline;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;

import java.nio.charset.StandardCharsets;

/**
 * Description:
 * Created on 2021/6/18 10:17 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ChannelPipelineDemo {

    public static void main(String[] args) {
        modifyPipeline();

        // evnets via Channel，流经整个pipeline
        ChannelHandlerContext ctx = null;
        Channel channel = ctx.channel();
        channel.write(Unpooled.copiedBuffer("netty in action", StandardCharsets.UTF_8));

        // events via ChannelPipeline，流经整个pipeline
        ChannelPipeline pipeline = ctx.pipeline();
        pipeline.write(Unpooled.copiedBuffer("netty in action", StandardCharsets.UTF_8));

        // events via ChannelPipeline，从当前ctx的下个开始处理
        ctx.write(Unpooled.copiedBuffer("netty in action", StandardCharsets.UTF_8));
    }

    // modify the channelPipeline
    private static void modifyPipeline() {
        ChannelPipeline pipeline = null;
        FirstHandler firstHandler = new FirstHandler();
        pipeline.addLast("handler1", firstHandler);
        pipeline.addFirst("handler2", new SecondHandler());
        pipeline.addLast("handler3", new ThirdHanlder());

        pipeline.remove("handler3");
        pipeline.remove(firstHandler);

        pipeline.replace("handler2", "handler4", new FourthHandler());
    }

    private static class FirstHandler extends ChannelInboundHandlerAdapter {
    }

    private static class SecondHandler extends ChannelInboundHandlerAdapter {
    }

    private static class ThirdHanlder extends ChannelInboundHandlerAdapter {
    }

    private static class FourthHandler extends ChannelInboundHandlerAdapter {
    }
}
