/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.wolf.concurrenttest.jjzl.sendqueue;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

// 设定高水位
public class QueueClientHandler2 extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(QueueClientHandler2.class);

    private final ByteBuf firstMessage;

    Runnable loadRunner;

    AtomicLong sendSum = new AtomicLong(0);

    Runnable profileMonitor;

    static final int SIZE = Integer.parseInt(System.getProperty("size", "256"));

    /**
     * Creates a client-side handler.
     */
    public QueueClientHandler2() {
        firstMessage = Unpooled.buffer(SIZE);
        for (int i = 0; i < firstMessage.capacity(); i++) {
            firstMessage.writeByte((byte) i);
        }
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        ctx.channel().config().setWriteBufferHighWaterMark(10 * 1024 * 1024);
        loadRunner = () -> {
            try {
                TimeUnit.SECONDS.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ByteBuf msg = null;
            final int len = "Netty OOM Example".getBytes().length;
            while (true) {// 10s就行
                if (ctx.channel().isWritable()) {
                    msg = Unpooled.wrappedBuffer("Netty OOM Example".getBytes());
                    ctx.writeAndFlush(msg);
                } else {
                    logger.warn("The write queue is busy: " +
                            ctx.channel().unsafe().outboundBuffer().nioBufferSize());
                }
            }
        };
        new Thread(loadRunner, "LoadRunnerClientHandler-Thread").start();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ReferenceCountUtil.release(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
