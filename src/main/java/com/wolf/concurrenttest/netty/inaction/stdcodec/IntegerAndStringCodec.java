package com.wolf.concurrenttest.netty.inaction.stdcodec;

import io.netty.channel.CombinedChannelDuplexHandler;

/**
 * Description：组合inboundDecoder和outboundEncoder
 * <br/> Created on 9/23/17 10:16 AM
 *
 * @author 李超
 * @since 1.0.0
 */
public class IntegerAndStringCodec extends CombinedChannelDuplexHandler<IntegerToStringDecoder, IntegerToStringEncoder> {
    public IntegerAndStringCodec() {
        super(new IntegerToStringDecoder(), new IntegerToStringEncoder());
    }
}
