package com.wolf.concurrenttest.netty.inaction.pipeline;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * Description: 演示存储ctx，ctx是线程安全的，可以后面使用
 * Created on 2021/6/19 7:21 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class StoreCtxHandler extends ChannelHandlerAdapter {
    private ChannelHandlerContext ctx;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
    }

    public void send(String msg) {
        ctx.write(msg);
    }
}
