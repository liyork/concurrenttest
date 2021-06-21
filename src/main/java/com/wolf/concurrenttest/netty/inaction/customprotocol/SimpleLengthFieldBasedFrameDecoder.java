package com.wolf.concurrenttest.netty.inaction.customprotocol;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * Description: 简单演示-自定义的基于长度的解码器
 * <br/> Created on 2018/7/19 18:31
 *
 * @author 李超
 * @since 1.0.0
 */
public class SimpleLengthFieldBasedFrameDecoder extends LengthFieldBasedFrameDecoder {

    public SimpleLengthFieldBasedFrameDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }

    // in是流中的所有数据，自己进行下面拆分
    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        int packageLength = (int) in.getUnsignedInt(0);//获取文件头部，不移动readerIndex/writerIndex
        if (in.readableBytes() < packageLength) {//当ByteBuf没有达到长度时，return null，等待下次再操作
            return null;
        }

        in.skipBytes(4);//舍弃头部
        ByteBuf frame = in.slice(in.readerIndex(), packageLength).retain();//取出自己定义的packet包返回给后面的channelHandler

        //这一步一定要有，不然其实bytebuf的readerIndex没有变，netty会一直从这里开始读取，将readerIndex移动就相当于把前面的数据处理过了废弃掉了。
        in.readerIndex(packageLength);
        return frame;
    }

}
