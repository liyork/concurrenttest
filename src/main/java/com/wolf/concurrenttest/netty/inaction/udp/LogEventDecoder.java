package com.wolf.concurrenttest.netty.inaction.udp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Description: 解码byte->LogEvent
 * Created on 2021/6/23 1:02 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class LogEventDecoder extends MessageToMessageDecoder<DatagramPacket> {
    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket datagramPacket, List<Object> out) throws Exception {
        ByteBuf data = datagramPacket.content();
        int separatorIndex = data.indexOf(0, data.readableBytes(), LogEvent.SEPARATOR);
        String fileName = data.slice(0, separatorIndex).toString(StandardCharsets.UTF_8);
        String logMsg = data.slice(separatorIndex + 1, data.readableBytes()).toString(StandardCharsets.UTF_8);

        LogEvent event = new LogEvent(datagramPacket.recipient(), System.currentTimeMillis(), fileName, logMsg);
        out.add(event);
    }
}
