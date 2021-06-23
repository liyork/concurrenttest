package com.wolf.concurrenttest.netty.inaction.udp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Description: 针对outbound，LogEvent编码器
 * 这里操作的是ByteBuf，最后放入了DatagramPacket，
 * 最后写出时HeadContext.flush->unsafe.flush->NioDatagramChannel.doWrite->envelope.content->javaChannel().send(nioData, remoteAddress)
 * Created on 2021/6/23 9:21 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class LogEventEncoder extends MessageToMessageEncoder<LogEvent> {

    private final InetSocketAddress remoteAddress;

    public LogEventEncoder(InetSocketAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, LogEvent logEvent, List<Object> out) throws Exception {
        ByteBuf buf = ctx.alloc().buffer();
        // write the filename
        buf.writeBytes(logEvent.getLogfile().getBytes(CharsetUtil.UTF_8));
        // separate
        buf.writeByte(LogEvent.SEPARATOR);
        // write the actual log messages
        buf.writeBytes(logEvent.getMsg().getBytes(StandardCharsets.UTF_8));
        // add DatagramPacket to the List of encoded messages
        out.add(new DatagramPacket(buf, remoteAddress));
    }
}
