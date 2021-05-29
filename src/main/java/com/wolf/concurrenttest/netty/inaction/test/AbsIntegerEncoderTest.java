package com.wolf.concurrenttest.netty.inaction.test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Assert;
import org.junit.Test;

/**
 * Description:
 * <br/> Created on 9/26/17 8:06 AM
 *
 * @author 李超
 * @since 1.0.0
 */
public class AbsIntegerEncoderTest {

    @Test
    public void testEncoded() {
        ByteBuf buf = Unpooled.buffer();
        for (int i = 1; i < 10; i++) {
            buf.writeInt(i * -1);
        }
        EmbeddedChannel channel = new EmbeddedChannel(
                new AbsIntegerEncoder());


        Assert.assertTrue(channel.writeOutbound(buf));//先模拟发送，让handler处理
        Assert.assertTrue(channel.finish());

        Object outbound = channel.readOutbound();//模拟读取数据，看看是否正确
        while (null != outbound) {
            System.out.println(outbound);
            outbound = channel.readOutbound();
        }

    }
}
