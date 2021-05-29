package com.wolf.concurrenttest.netty.inaction.customprotocol;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.nio.ByteOrder;

/**
 * Description:
 * <br/> Created on 2018/7/19 18:31
 *
 * @author 李超
 * @since 1.0.0
 */
public class MyLengthFieldBasedFrameDecoder extends LengthFieldBasedFrameDecoder {

    private ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;
    private int maxFrameLength = 1024 * 10;
    private int lengthFieldLength = 4;
    private int initialBytesToStrip = 0;
    private long tooLongFrameLength;
    private long bytesToDiscard;
    private boolean failFast = true;


    public MyLengthFieldBasedFrameDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {

        int packageLength = (int) in.getUnsignedInt(0);//获取头部，不移动readerIndex/writerIndex
        if (in.readableBytes() < packageLength) {//当ByteBuf没有达到长度时，return null
            return null;
        }

        in.skipBytes(4);//舍弃头部
        int index = in.readerIndex();
        ByteBuf frame = in.slice(index, packageLength).retain();//取出自己定义的packet包返回给ChannelRead

        in.readerIndex(packageLength);//这一步一定要有，不然其实bytebuf的readerIndex没有变，netty会一直从这里开始读取，将readerIndex移动就相当于把前面的数据处理过了废弃掉了。
        return frame;
    }

}
