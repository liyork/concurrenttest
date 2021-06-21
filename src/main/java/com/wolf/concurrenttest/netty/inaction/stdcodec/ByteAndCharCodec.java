package com.wolf.concurrenttest.netty.inaction.stdcodec;

import io.netty.channel.CombinedChannelDuplexHandler;

/**todo 与IntegerAndStringCodec重复，后期删除一个
 * Description: 演示用CombinedChannelDuplexHandler，相比xCodec更加灵活
 * Created on 2021/6/20 10:15 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ByteAndCharCodec extends CombinedChannelDuplexHandler<ByteToCharDecoder, CharToByteEncoder> {
    public ByteAndCharCodec() {
        super(new ByteToCharDecoder(), new CharToByteEncoder());
    }
}
