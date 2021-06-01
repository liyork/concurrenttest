package com.wolf.concurrenttest.netty.inaction.niodemo;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Description:
 * Created on 2021/6/2 9:02 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ByteBufferTest {
    public static void main(String[] args) throws IOException {
        SocketChannel inChannel = null;

        ByteBuffer buf = ByteBuffer.allocate(48);

        int bytesRead = -1;
        do {
            bytesRead = inChannel.read(buf);
            if (bytesRead != -1) {
                buf.flip();// make buffer ready for read
                while (buf.hasRemaining()) {
                    System.out.println((char) buf.get());
                }
                buf.clear();
            }
        } while (bytesRead != -1);
        inChannel.close();
    }
}
