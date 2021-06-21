package com.wolf.concurrenttest.netty.inaction.transport;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Description:
 * Created on 2021/6/17 10:25 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ChannelDemo {
    public static void main(String[] args) {
        write();

        channelThreadSafe();
    }

    // using the channel from many threads
    private static void channelThreadSafe() {
        Channel channel = null;// Channel is thread-safe
        ByteBuf buf = Unpooled.copiedBuffer("your data", StandardCharsets.UTF_8);
        Runnable writer = () -> {
            channel.write(buf.duplicate());
        };
        ExecutorService executor = Executors.newCachedThreadPool();
        // the messages are written in the same order as you passed them to the write method
        executor.execute(writer);// write in one thread
        executor.execute(writer);// write in another thread
    }

    // writing to a channel
    private static void write() {
        Channel channel = null;
        ByteBuf buf = Unpooled.copiedBuffer("your data", StandardCharsets.UTF_8);
        ChannelFuture cf = channel.write(buf);

        cf.addListener((ChannelFutureListener) future -> {// add ChannelFutureListener to get notified after write completes
            if (future.isSuccess()) {
                System.out.println("Write successful");
            } else {
                System.err.println("Write error");
                future.cause().printStackTrace();
            }
        });
    }
}
