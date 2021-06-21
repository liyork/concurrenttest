package com.wolf.concurrenttest.netty.inaction.pipeline;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.util.ReferenceCountUtil;

/**
 * Description: 演示outboundHandler
 * <p>
 * all of the methods take a Channelpromise as an argument that must be notified once the request should stop to get forwarded through the ChannelPipeline.
 * if you handle a write operation and discard a message it's you responsible to release it probably.
 * if the ChannelPromise is not notified it may lead to situations where a ChannelFutureListener is not notified about a handled message
 * it's the responsiblity of the user to call release if the message is consumed and not passed
 * to the next ChannelOutboundHandler in the ChannelPipeline.
 * Once the message is passed over to the actual Transport it will be released automatically
 * by it once the message was written or the Channel was closed.
 * 似乎想要拦截写出动作就要stop+自己释放，要是不拦截，那么到实际javaSocket那里后悔自动释放
 * Created on 2021/6/19 1:24 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class DiscardOutBoundHandler extends ChannelOutboundHandlerAdapter {
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {

        ReferenceCountUtil.release(msg);
        promise.setSuccess();// notify ChannelPromise that data handled
    }
}
