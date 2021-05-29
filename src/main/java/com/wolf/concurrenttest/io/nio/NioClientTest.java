package com.wolf.concurrenttest.io.nio;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Description:
 * <br/> Created on 2017/5/31 17:12
 *
 * @author 李超
 * @since 1.0.0
 */
public class NioClientTest {

    //创建缓冲区
    private ByteBuffer buffer = ByteBuffer.allocate(512);
    //访问服务器

    public void connectShort(String host, int port) throws IOException {
        InetSocketAddress address = new InetSocketAddress(InetAddress.getByName(host), port);
        SocketChannel socket = null;
        byte[] bytes = new byte[512];
        while(true) {
            try {
                System.in.read(bytes);
                socket = SocketChannel.open();//创建SocketChannel和socket但是并未连接
                socket.connect(address);
                buffer.clear();
                buffer.put(bytes);
                buffer.flip();
                socket.write(buffer);
                buffer.clear();

                socket.read(buffer);
                buffer.flip();
                System.out.println(new String(buffer.array(), "utf-8"));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if(socket != null) {
                    socket.close();
                }
            }
        }
    }

    public void connectLong(String host, int port) throws IOException {
        InetSocketAddress address = new InetSocketAddress(InetAddress.getByName(host), port);
        SocketChannel socket = SocketChannel.open();//创建SocketChannel和socket但是并未连接
        socket.connect(address);
        byte[] bytes = new byte[512];
        while(true) {
            try {
                System.in.read(bytes);
                buffer.clear();
                buffer.put(bytes);
                buffer.flip();
                socket.write(buffer);
                buffer.clear();

                //教训：这个socket是阻塞的，如果服务器没有任何返回他会一直卡住，并且后续客户端输入内容也是卡住的
                socket.read(buffer);
                buffer.flip();
                System.out.println(new String(buffer.array(), "utf-8"));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {

            }
        }
    }

    public static void main(String[] args) throws IOException {
        test1();
//        test2();
    }

    private static void test1() throws IOException {
        new NioClientTest().connectLong("localhost", 8099);
    }

    private static void test2() throws IOException {
        new NioClientTest().connectLong("localhost", 9099);
    }
}
