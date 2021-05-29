package com.wolf.concurrenttest.netty.inaction;

import io.netty.channel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Description:zero-memory-copy,This only works if the content of the Fille needs to be transfered as is
 * <br/> Created on 9/25/17 2:13 AM
 *
 * @author 李超
 * @since 1.0.0
 */
public class TransmitBigFile {
    public static void main(String[] args) throws FileNotFoundException {
        Channel channel = null;
        File file = null;
        FileInputStream in = new FileInputStream(file);
        FileRegion region = new DefaultFileRegion(
                in.getChannel(), 0, file.length());
        channel.writeAndFlush(region)
                .addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future)
                            throws Exception {
                        if (!future.isSuccess()) {
                            Throwable cause = future.cause();
                            // Do something
                        }
                    }
                });
    }
}
