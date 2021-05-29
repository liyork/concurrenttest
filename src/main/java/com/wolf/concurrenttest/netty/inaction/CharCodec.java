package com.wolf.concurrenttest.netty.inaction;

import io.netty.channel.CombinedChannelDuplexHandler;

/**
 * Description：组合
 * <br/> Created on 9/23/17 10:16 AM
 *
 * @author 李超
 * @since 1.0.0
 */
public class CharCodec extends
        CombinedChannelDuplexHandler<IntegerToStringDecoder, IntegerToStringEncoder> {
    public CharCodec() {
        super(new IntegerToStringDecoder(), new IntegerToStringEncoder());
    }
}
