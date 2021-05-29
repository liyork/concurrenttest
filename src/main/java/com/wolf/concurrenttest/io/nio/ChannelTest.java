package com.wolf.concurrenttest.io.nio;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 * <p> Description:
 * <p/>
 * Date: 2015/10/26
 * Time: 8:38
 *
 * @author 李超
 * @version 1.0
 * @since 1.0
 */
public class ChannelTest {


    @Test
    public void testRead() throws Exception {
        FileInputStream fin = new FileInputStream("d:\\test.txt");

        // 获取通道
        FileChannel fc = fin.getChannel();

        // 创建缓冲区
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        // 读取数据到缓冲区
        fc.read(buffer);

        buffer.flip();

        while(buffer.hasRemaining()) {
            byte b = buffer.get();
            System.out.print(((char) b));
        }

        //fc.write(buffer);读的Channel不能写入
        fin.close();
    }

    @Test
    public void testWrite() throws IOException {
        final byte message[] = {83, 111, 109, 101, 32, 98, 121, 116, 101, 115};

        FileOutputStream fout = new FileOutputStream("c:\\test.txt");

        FileChannel fc = fout.getChannel();

        ByteBuffer buffer = ByteBuffer.allocate(1024);

        buffer.put(message);

        //需要重置pos
        buffer.flip();

        fc.write(buffer);

        fout.close();
    }

    public static void main(String[] args) throws IOException {
        ReadableByteChannel source = Channels.newChannel(System.in);
        WritableByteChannel dest = Channels.newChannel(System.out);

        channelCopy1(source, dest);
// alternatively, call channelCopy2 (source, dest);

        source.close();
        dest.close();
    }

    /**
     * Channel copy method 1. This method copies data from the src
     * channel and writes it to the dest channel until EOF on src.
     * This implementation makes use of compact( ) on the temp buffer
     * to pack down the data if the buffer wasn't fully drained. This
     * may result in data copying, but minimizes system calls. It also
     * requires a cleanup loop to make sure all the data gets sent.
     */
    private static void channelCopy1(ReadableByteChannel src, WritableByteChannel dest) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocateDirect(16 * 1024);
        while(src.read(buffer) != -1) {
// Prepare the buffer to be drained
            buffer.flip();
// Write to the channel; may block
            dest.write(buffer);//可能不会一次写全
// If partial transfer, shift remainder down
// If buffer is empty, same as doing clear( )
          buffer.compact();
        }
// EOF will leave buffer in fill state
        buffer.flip();
// Make sure that the buffer is fully drained
        while(buffer.hasRemaining()) {
            dest.write(buffer);
        }
    }

    /**
     * Channel copy method 2. This method performs the same copy, but
     * assures the temp buffer is empty before reading more data. This
     * never requires data copying but may result in more systems calls.
     * No post-loop cleanup is needed because the buffer will be empty
     * when the loop is exited.
     */
    private static void channelCopy2(ReadableByteChannel src, WritableByteChannel dest) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocateDirect(16 * 1024);
        while(src.read(buffer) != -1) {
// Prepare the buffer to be drained
            buffer.flip();
// Make sure that the buffer was fully drained
            while(buffer.hasRemaining()) {
                dest.write(buffer);
            }
// Make the buffer empty, ready for filling
            buffer.clear();
        }
    }
}

