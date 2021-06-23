package com.wolf.concurrenttest.netty.inaction.udp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;

/**
 * Description: udp广播者，扫描文件进行广播
 * 方式1：todo 这种似乎没有收到呢
 * listen on specific port and just print all data received to STDOUT.
 * 1. listen for UDP data on port 9090=>nc -l -u 9090
 * 2. 运行程序LogEventBroadcaster
 * 方式2：
 * 1.启动LogEventMonitor
 * 2.启动LogEventBroadcaster
 * 3.可以修改logfile.txt(是运行时的那个文件)中内容保存，观看结果
 * <p>
 * <p>
 * Created on 2021/6/23 9:27 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class LogEventBroadcaster {
    private final EventLoopGroup group;
    private final Bootstrap bootstrap;
    private final File file;

    public LogEventBroadcaster(InetSocketAddress address, File file) {
        this.group = new NioEventLoopGroup();
        this.bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, true)// work with broadcasts
                .handler(new LogEventEncoder(address));
        this.file = file;
    }

    public void run() throws IOException {
        // bind and get the channel so we can use it.
        // not that there is not connect when use DatagramChannel as those are connectionless.
        Channel ch = bootstrap.bind(0).syncUninterruptibly().channel();
        long pointer = 0;
        for (; ; ) {
            long len = file.length();
            if (len < pointer) {// file was reset
                pointer = len;// set the current filepointer to the last byte of the file as we only want to broadcast new log entries ad not what was written before teh application was started
            } else if (len > pointer) {// Content was added
                RandomAccessFile raf = new RandomAccessFile(file, "r");
                raf.seek(pointer);// set the current filepointer so nothing old is send
                String line;
                while ((line = raf.readLine()) != null) {
                    ch.writeAndFlush(new LogEvent(null, -1, file.getAbsolutePath(), line));
                }
                pointer = raf.getFilePointer();// store the current position within the file so we can later continue here(用于稍后动态增减文件内容时判断)
                raf.close();
            }
            try {
                Thread.sleep(1000);// sleep for 1 second and then check if there is something new in the logfile which can send
            } catch (InterruptedException e) {
                Thread.interrupted();
                break;
            }
        }
    }

    public void stop() {
        group.shutdownGracefully();
    }

    public static void main(String[] args) throws IOException {
        InetSocketAddress address = new InetSocketAddress("255.255.255.255", 9090);
        String filePath = LogEventBroadcaster.class.getClassLoader().getResource("logfile.txt").getFile();
        File file = new File(filePath);
        LogEventBroadcaster broadcaster = new LogEventBroadcaster(address, file);
        try {
            broadcaster.run();
        } finally {
            broadcaster.stop();
        }
    }
}
