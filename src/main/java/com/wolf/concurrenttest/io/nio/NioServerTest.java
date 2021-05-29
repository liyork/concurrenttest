package com.wolf.concurrenttest.io.nio;

import com.alibaba.fastjson.JSON;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;

/**
 * Description:
 * Server端通常由一个thread来监听connect事件，另外多个thread来监听读写事件。
 * 这样做的好处是这些连接只有在真是请求的时候才会创建thread来处理，one request one thread。
 * 这种方式在server端需要支持大量连接但这些连接同时发送请求的峰值不会很多的时候十分有效。
 *
 * 在NIO的处理方式中，当一个请求来的话，开启线程进行处理，可能会等待后端应用的资源(JDBC连接等)，
 * 其实这个线程就被阻塞了，当并发上来的话，还是会有BIO一样的问题。
 *
 * OP_ACCEPT可以一直保持，OP_WRITE和OP_READ只能保持一个
 * <br/> Created on 2017/5/31 17:04
 *
 * @author 李超
 * @since 1.0.0
 */
public class NioServerTest implements Runnable {

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

    //缓冲区
    private ByteBuffer buf = ByteBuffer.allocate(512);

    public NioServerTest() {
        init();
    }

    /**
     * 这个method的作用
     * 1：是初始化选择器
     * 2：打开两个通道
     * 3：给通道上绑定一个socket
     * 4：将选择器注册到通道上
     */
    public void init() {
        try {
            //创建选择器
            this.selector = SelectorProvider.provider().openSelector();
            //打开第一个服务器通道
            this.serversocket1 = ServerSocketChannel.open();
            //告诉程序现在不是阻塞方式的
            this.serversocket1.configureBlocking(false);
            //获取现在与该通道关联的套接字
            this.serversocket1.socket().bind(new InetSocketAddress("localhost", this.port1));
            //将选择器注册到通道上，返回一个选择键
            //OP_ACCEPT用于套接字接受操作的操作集位
            this.serversocket1.register(this.selector, SelectionKey.OP_ACCEPT);

            //然后初始化第二个服务端
            this.serversocket2 = ServerSocketChannel.open();
            this.serversocket2.configureBlocking(false);
            this.serversocket2.socket().bind(new InetSocketAddress("localhost", this.port2));
            this.serversocket2.register(this.selector, SelectionKey.OP_ACCEPT);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 客户端连接服务器
     * 只有ServerSocketChannel 支持 OP_ACCEPT 操作
     *
     * @throws IOException
     */
    public void accept(SelectionKey key) throws IOException {
        ServerSocketChannel server = (ServerSocketChannel) key.channel();
        if(server.equals(serversocket1)) {
            clientchannel1 = server.accept();//如果没有连接进来，立即返回null
            clientchannel1.configureBlocking(false);
            //OP_READ用于读取操作的操作集位,注册上了就会有不会被自动取消
            clientchannel1.register(this.selector, SelectionKey.OP_READ);
            //只能注册一种，后者会进行覆盖
            //clientchannel1.register(this.selector, SelectionKey.OP_WRITE);
        } else {
            clientchannel2 = server.accept();
            clientchannel2.configureBlocking(false);
            //OP_READ用于读取操作的操作集位
            clientchannel2.register(this.selector, SelectionKey.OP_READ);
        }
    }

    /**
     * 从通道中读取数据
     * 并且判断是给那个服务通道的
     *
     * @throws IOException
     */
    public void read(SelectionKey key) throws IOException {

        this.buf.clear();
        //通过选择键来找到之前注册的通道
        //但是这里注册的是ServerSocketChannel为什么会返回一个SocketChannel？？
        SocketChannel channel = (SocketChannel) key.channel();
        //从通道里面读取数据到缓冲区并返回读取字节数
        int count = 0;

        try {
            //可能一次读不全，一直读到没有为止，可能吗？？？
            while((count = channel.read(this.buf)) > 0) {
            }
        } catch (IOException e) {
            //客户端如果未调用socket.close();而是直接强制关闭了连接则channel.read时会出现异常。
            e.printStackTrace();
            cancleChannel(key, count);
            return;
        }

        //客户端调用了socket.close();
        if(count < 0) {
            cancleChannel(key, count);
            return;
        }
        buf.flip();
        System.out.println("buf.remaining():"+buf.remaining());
        //将数据从缓冲区中拿出来
        String input = new String(this.buf.array(), 0, buf.remaining(), "utf-8").trim();
        //那么现在判断是连接的那种服务
        if(channel.equals(this.clientchannel1)) {
            System.out.println("欢迎您使用服务A");
            System.out.println("您的输入为：" + input);
        } else {
            System.out.println("欢迎您使用服务B");
            System.out.println("您的输入为：" + input);
        }

        //覆盖注册，我要写
        channel.register(selector, SelectionKey.OP_WRITE);
    }

    //关闭的是SocketChannel，serverSocketChannel还继续服务
    private void cancleChannel(SelectionKey key, int count) throws IOException {
        System.out.println("count:" + count);
        //取消这个通道的注册
        key.channel().close();
        key.cancel();
    }

    /**
     * 从通道中写入数据
     * 并且判断是给那个服务通道的
     *
     * @throws IOException
     */
    public void write(SelectionKey key) throws IOException {

        this.buf.clear();
        //通过选择键来找到之前注册的通道
        //但是这里注册的是ServerSocketChannel为什么会返回一个SocketChannel？？
        SocketChannel channel = (SocketChannel) key.channel();

        //那么现在判断是连接的那种服务
        if(channel.equals(this.clientchannel1)) {
            buf.put("bye-byea".getBytes("utf-8"));
        } else {
            buf.put("bye-byeb".getBytes("utf-8"));
        }
        buf.flip();
        channel.write(this.buf);
        //覆盖注册，我要读，让读的方法对此channel进行取消
        channel.register(selector, SelectionKey.OP_READ);

    }

    /**
     * 第一次(这次是由于注册了OP_ACCEPT)：SelectionKey-->{"acceptable":true,"connectable":false,"readable":false,"selector":{"open":true},"valid":true,"writable":false}
     * 第二次(这次是由于注册了OP_READ)：SelectionKey-->{"acceptable":false,"connectable":false,"readable":true,"selector":{"open":true},"valid":true,"writable":false}
     * 第三次(这次是由于socket关了)：SelectionKey-->{"acceptable":false,"connectable":false,"readable":true,"selector":{"open":true},"valid":true,"writable":false}
     */
    @Override
    public void run() {
        while(true) {
            try {
                System.out.println("selector begin... ");
                //选择一组键，其相应的通道已为 I/O 操作准备就绪。
                int select = this.selector.select();//卡住
                if(select == 0) {//返回socket的个数
                    continue;
                }
                System.out.println("selector end ... ");

                //返回此选择器的已选择键集
                //public abstract Set<SelectionKey> selectedKeys()
                Iterator selectorKeys = this.selector.selectedKeys().iterator();
                while(selectorKeys.hasNext()) {
                    //这里找到当前的选择键
                    SelectionKey key = (SelectionKey) selectorKeys.next();
                    System.out.println("SelectionKey-->" + JSON.toJSONString(key));
                    //然后将它从返回键队列中删除
                    //这里删除了，然后我在read中没有再注册，为什么还能select到？？？
                    selectorKeys.remove();
                    if(!key.isValid()) { // 选择键无效
                        continue;
                    }
                    if(key.isAcceptable()) {//new connection
                        this.accept(key);
                    } else if(key.isReadable()) {//channel有数据
                        this.read(key);
                    } else if(key.isWritable()) {
                        this.write(key);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        NioServerTest NioServer = new NioServerTest();
        Thread thread = new Thread(NioServer);
        thread.start();
    }
}