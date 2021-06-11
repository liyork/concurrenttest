package com.wolf.concurrenttest.jjzl.gateway;

import ch.qos.logback.core.util.TimeUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Description: 有问题，总是分配64kb数组不论请求多大
 * Created on 2021/6/1 9:17 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ApiGatewayServerHandler1 extends ChannelInboundHandlerAdapter {
    ExecutorService executorService = Executors.newFixedThreadPool(18);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ctx.write(msg);
        char[] req = new char[64 * 1024];
        executorService.execute(() -> {
            char[] dispatchReq = req;
            // 模拟业务处理
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
