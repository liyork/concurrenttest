package com.wolf.concurrenttest.jjzl.channelhandler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 可共享的handler，需要自身保证并发安全，尽量少用。
 */
@ChannelHandler.Sharable
public class SharableClientHandler extends ChannelInboundHandlerAdapter {
}
