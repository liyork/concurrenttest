package com.wolf.concurrenttest.io.nio.formal;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * <p> Description:
 * <p/>
 * Date: 2015/10/26
 * Time: 16:14
 *
 * @author 李超
 * @version 1.0
 * @since 1.0
 */
public class EchoClient implements Runnable {

    // 空闲计数器,如果空闲超过10次,将检测server是否中断连接.

    private static int idleCounter = 0;

    private Selector selector;

    private SocketChannel socketChannel;

    private ByteBuffer temp = ByteBuffer.allocate(1024);


    public static void main(String[] args) throws IOException {

        EchoClient client = new EchoClient();
        new Thread(client).start();
        //client.sendFirstMsg();
    }


    public EchoClient() throws IOException {

        // 同样的,注册闹钟.
        this.selector = Selector.open();
        socketChannel.configureBlocking(false);
        // 连接远程server
        socketChannel = SocketChannel.open();
        // 如果快速的建立了连接,返回true.如果没有建立,则返回false,并在连接后出发Connect事件.
        Boolean isConnected = socketChannel.connect(new InetSocketAddress("localhost", 7878));

        SelectionKey key = null;
        if (isConnected) {
             key = socketChannel.register(selector, SelectionKey.OP_READ);
            this.sendFirstMsg();
        } else {
            // 如果连接还在尝试中,则注册connect事件的监听. connect成功以后会触发connect事件.
            key.interestOps(SelectionKey.OP_CONNECT);
//            socketChannel.register(selector,SelectionKey.OP_CONNECT)//和上面一样？
        }
    }


    public void sendFirstMsg() throws IOException {
        String msg = "Hello NIO.";
        ByteBuffer wrap = ByteBuffer.wrap(msg.getBytes(Charset.forName("UTF-8")));
        socketChannel.write(wrap);
        if (!wrap.hasRemaining()) {
            System.out.println("send echo content to server succeed!");
        }
    }

    @Override
    public void run() {

        while (true) {
            try {
                if (!selector.isOpen()) {
                    break;
                }

                // 阻塞,等待事件发生,或者1秒超时. num为发生事件的数量.
                int num = this.selector.select(1000);
                if (num == 0) {
                    idleCounter++;
                    if (idleCounter > 10) {
                        // 如果server断开了连接,发送消息将失败.
                        try {
                            this.sendFirstMsg();
                        } catch (ClosedChannelException e) {
                            e.printStackTrace();
                            this.socketChannel.close();
                            return;
                        }
                    }

                    continue;
                } else {
                    idleCounter = 0;
                }

                Set<SelectionKey> keys = this.selector.selectedKeys();
                Iterator<SelectionKey> it = keys.iterator();

                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    it.remove();

                    if (!key.isValid()) {
                        continue;
                    }

                    if (key.isConnectable()) {
                        // socket connected
                        SocketChannel sc = (SocketChannel) key.channel();
                        if (sc.isConnectionPending()) {
                            sc.finishConnect();
                        } else if (sc.finishConnect()) {
                            sc.register(selector, SelectionKey.OP_READ);
                            this.sendFirstMsg();
                        }
                    }

                    if (key.isReadable()) {
                        // msg received.
                        SocketChannel sc = (SocketChannel) key.channel();
                        this.temp = ByteBuffer.allocate(1024);
                        int count = sc.read(temp);
                        if (count < 0) {
                            sc.close();
                            key.cancel();
                            continue;
                        } else if (count == 0) {
                            continue;
                        }

                        // 切换buffer到读状态,内部指针归位.
                        temp.flip();

                        String msg = Charset.forName("UTF-8").decode(temp).toString();

                        System.out.println("Client received [" + msg + "] from server address:" + sc.socket().getRemoteSocketAddress());

                        Thread.sleep(1000);

                        // echo back.
                        sc.write(ByteBuffer.wrap(msg.getBytes(Charset.forName("UTF-8"))));
                        // 清空buffer
                        temp.clear();
                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                if (null != selector) {
                    try {
                        selector.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


}
