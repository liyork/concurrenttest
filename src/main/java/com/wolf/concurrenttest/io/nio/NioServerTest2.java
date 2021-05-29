package com.wolf.concurrenttest.io.nio;

import com.alibaba.fastjson.JSON;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.channels.spi.SelectorProvider;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Description:
 * nio不会由于客户端网络等条件的慢写入而受到影响。
 *
 * io操作准备好时，得到通知。io操作本身还是同步的。
 * <br/> Created on 2017/5/31 17:04
 *
 * @author 李超
 * @since 1.0.0
 */
public class NioServerTest2 implements Runnable {

    Map<SelectableChannel, Long> timeCollect = new HashMap<>();

    //第一个端口
    private Integer port1 = 8099;
    //第二个端口
    private Integer port2 = 9099;
    //第一个服务器通道 服务A
    private ServerSocketChannel serversocket1;
    //第二个服务器通道 服务B
    private ServerSocketChannel serversocket2;
    //连接1
    private SocketChannel clientchannel1;
    //连接2
    private SocketChannel clientchannel2;

    //选择器，主要用来监控各个通道的事件
    private Selector selector;

    ExecutorService executorService;

    //读缓冲区
    private ByteBuffer readBuf = ByteBuffer.allocate(512);
    //写缓冲区
    private ByteBuffer writeBuf = ByteBuffer.allocate(512);

    public NioServerTest2() {
        init();
    }

    public void init() {
        try {
            //连接队列大小
            int backLog = 1024;
            this.selector = SelectorProvider.provider().openSelector();
            this.serversocket1 = ServerSocketChannel.open();
            this.serversocket1.configureBlocking(false);
            this.serversocket1.socket().bind(new InetSocketAddress("localhost", this.port1),backLog);
            this.serversocket1.register(this.selector, SelectionKey.OP_ACCEPT);

            this.serversocket2 = ServerSocketChannel.open();
            this.serversocket2.configureBlocking(false);
            this.serversocket2.socket().bind(new InetSocketAddress("localhost", this.port2),backLog);
            this.serversocket2.register(this.selector, SelectionKey.OP_ACCEPT);

            executorService = Executors.newFixedThreadPool(10);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void accept(SelectionKey key) throws IOException {
        ServerSocketChannel server = (ServerSocketChannel) key.channel();
        if(server.equals(serversocket1)) {
            clientchannel1 = server.accept();//如果没有连接进来，立即返回null
            clientchannel1.configureBlocking(false);
            clientchannel1.register(this.selector, SelectionKey.OP_READ);
        } else {
            clientchannel2 = server.accept();
            clientchannel2.configureBlocking(false);
            clientchannel2.register(this.selector, SelectionKey.OP_READ);
        }
    }

    /**
     * channel中如果有数据，即使remove这个key好像还是会被select到
     * 两个概念：
     * 需要iterator.remove的是本次select到的关心的channel准备就绪了，移除本次选择的事件。
     * 如果channel中数据未提取，则下次select还是准备好的
     *
     * 注册关心事件时，OP_READ会被OP_WRITE取代，如果没有再注册OP_WRITE则还保持OP_READ
     *
     * 由于主线程可能比子线程执行快，导致子线程还没有读完channel中数据，主线程又触发了select读。。
     * 所以让主线程将数据读到buffer中，处理buff操作由子线程完成
     *
     * @throws IOException
     */
    public void read1(final SelectionKey key) throws IOException {

        this.readBuf.clear();
        final SocketChannel channel = (SocketChannel) key.channel();

        int count = 0;
        try {
            while((count = channel.read(readBuf)) > 0) {
            }
        } catch (IOException e) {
            e.printStackTrace();
            cancleChannel(key, count);
            return;
        }

        if(count < 0) {
            cancleChannel(key, count);
            return;
        }

        //上面将channel中数据写入到buffer中，这里开启多线程处理
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("execute:" + Thread.currentThread().getName());
                    readBuf.flip();
                    System.out.println("buf.remaining():" + readBuf.remaining());

                    String input = new String(readBuf.array(), 0, readBuf.remaining(), "utf-8").trim();

                    if(channel.equals(clientchannel1)) {
                        System.out.println("欢迎您使用服务A");
                        System.out.println("您的输入为：" + input);
                    } else {
                        System.out.println("欢迎您使用服务B");
                        System.out.println("您的输入为：" + input);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        channel.register(selector, SelectionKey.OP_WRITE);
    }

    /**
     * 按照网上指点，可以先取消主线程对于这个channel的读select，当子线程完成时，恢复注册读操作
     * @param key
     * @throws IOException
     */
    public void read2(final SelectionKey key) throws IOException {
        //取消读事件
        key.interestOps(key.interestOps() & (~SelectionKey.OP_READ));

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {

                    readBuf.clear();
                    final SocketChannel channel = (SocketChannel) key.channel();

                    Long aLong = timeCollect.get(channel);
                    if (aLong == null) {
                        timeCollect.put(channel, System.currentTimeMillis());
                    }

                    int count = 0;
                    try {
                        long start = System.currentTimeMillis();
                        while((count = channel.read(readBuf)) > 0) {
                            //时间很短，似乎nio的selector把都准备好的数据给到channel中，然后这里读取时根本不会受到客户端的网络慢影响。
                            //当前线程只有select的读事件触发时才会进来，也就是客户端不管多慢，我不用等他，等他发好了，我这边读事件就好了，我就读。
                            System.out.println("read from client:"+count);

                            //一开始看while还觉得挺厉害，但是仔细想就不对了，若真是读了多次，但是count总是被覆盖的。其实基本就是读一次
//                            readBuf.flip();
//                            byte[] bytes = new byte[readBuf.remaining()];
//                            readBuf.get(bytes);
//                            String body = new String(bytes, "utf-8");
//                            System.out.println("echo content:"+body);
                        }
                        System.out.println("read cost:"+(System.currentTimeMillis()-start));
                    } catch (IOException e) {
                        e.printStackTrace();
                        cancleChannel(key, count);
                        return;
                    }

                    if(count < 0) {
                        cancleChannel(key, count);
                        return;
                    }


                    System.out.println("execute:" + Thread.currentThread().getName());
                    readBuf.flip();
                    System.out.println("buf.remaining():" + readBuf.remaining());

                    String input = new String(readBuf.array(), 0, readBuf.remaining(), "utf-8").trim();

                    if(channel.equals(clientchannel1)) {
                        System.out.println("欢迎您使用服务A");
                        System.out.println("您的输入为：" + input);
                    } else {
                        System.out.println("欢迎您使用服务B");
                        System.out.println("您的输入为：" + input);
                    }


                    writeBuf.clear();

                    if(channel.equals(clientchannel1)) {
                        writeBuf.put("bye-byea".getBytes("utf-8"));
                    } else {
                        writeBuf.put("bye-byeb".getBytes("utf-8"));
                    }
                    writeBuf.flip();
                    channel.write(writeBuf);

                    //重新恢复读事件
                    key.interestOps(key.interestOps() | SelectionKey.OP_READ);
                    //可能之前主线程错过了这个channel的读事件，直接唤醒
                    key.selector().wakeup();

                    Long aLong1 = timeCollect.get(channel);
                    System.out.println(" current client:"+channel+" total cost:"+(System.currentTimeMillis()-aLong1));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //关闭的是SocketChannel，serverSocketChannel还继续服务
    private void cancleChannel(SelectionKey key, int count) throws IOException {
        System.out.println("cancleChannel count:" + count);
        //取消这个通道的注册
        key.channel().close();
        key.cancel();
    }

    //一开始使用同一个buff而且使用多线程，导致主线程将数据写成bye-byea，然后子线程读到数据错误
    public void write(SelectionKey key) throws IOException {

        this.writeBuf.clear();
        SocketChannel channel = (SocketChannel) key.channel();

        if(channel.equals(this.clientchannel1)) {
            writeBuf.put("bye-byea".getBytes("utf-8"));
        } else {
            writeBuf.put("bye-byeb".getBytes("utf-8"));
        }
        writeBuf.flip();
        channel.write(this.writeBuf);
        channel.register(selector, SelectionKey.OP_READ);
    }

    @Override
    public void run() {
        while(true) {
            try {
                System.out.println("selector begin... ");
                int select = this.selector.select(1000);
                if(select == 0) {
                    System.out.println("select == 0");
                    continue;
                }
                System.out.println("selector end ... ");

                Iterator selectorKeys = this.selector.selectedKeys().iterator();
                while(selectorKeys.hasNext()) {

                    SelectionKey key = (SelectionKey) selectorKeys.next();
                    System.out.println("SelectionKey-->" + JSON.toJSONString(key));

                    selectorKeys.remove();
                    if(!key.isValid()) {
                        continue;
                    }
                    try {
                        if (key.isAcceptable()) {//新接入
                            this.accept(key);
                        } else if (key.isReadable()) {
                            this.read2(key);
                        }
                        //一开始试验使用write，后来觉得在read里做了就完了
//                    else if(key.isWritable()) {
//                        this.write(key);
//                    }
                        //Thread.sleep(2000);调试使用
                    } catch (Exception e) {
                        if (null != key) {
                            key.cancel();
                            if (null != key.channel()) {
                                key.channel().close();
                            }
                        }

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                if (null != selector) {
                    try {
                        //selector关闭后，所有注册在上面的channel和pipe资源都被自动关闭。
                        selector.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        NioServerTest2 NioServer = new NioServerTest2();
        Thread thread = new Thread(NioServer);
        thread.start();
    }
}