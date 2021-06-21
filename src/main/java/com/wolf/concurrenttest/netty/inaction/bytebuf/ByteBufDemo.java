package com.wolf.concurrenttest.netty.inaction.bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;

import java.nio.ByteBuffer;

/**
 * Description:
 * 在pipeline中，对于读，若当前bytebuf不需要传递到后面则直接release，要是想继续传递则不用考虑释放问题，
 * 对于写可以让netty将其释放。
 * Created on 2021/6/18 7:30 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ByteBufDemo {
    public static void main(String[] args) {
        heapBuf();

        directBuf();

        composeUseJDK();

        compositeBuf();

        // access data
        ByteBuf buffer = null;
        for (int i = 0; i < buffer.capacity(); i++) {
            byte b = buffer.getByte(i);
            System.out.println((char) b);
        }

        // read data
        // iterates teh readable bytes of a buffer
        while (buffer.isReadable()) {
            System.out.println(buffer.readByte());
        }

        // write data
        // fills teh writable bytes of a buffer with 4 integers
        while (buffer.writableBytes() >= 4) {
            buffer.writeInt(4);
        }

        // clear, set both readerIndex and writerIndex to 0

        // ByteBufProcessor.bytesBefore，处理有Null结尾的数据

        // readerIndex/writerIndex直接设定index值

        // derived buffers
        // duplicate/slice/readOnly/order有独立的索引但是共享内容
        // copy拷贝都独立
    }

    private static void compositeBuf() {
        // CompositeByteBuf in action
        CompositeByteBuf compBuf = null;
        ByteBuf heapBuf = null;
        ByteBuf directBuf = null;

        compBuf.addComponents(heapBuf, directBuf);

        compBuf.removeComponent(0);

        for (ByteBuf buf : compBuf) {
            System.out.println(buf.toString());
        }

        // access data
        if (!compBuf.hasArray()) {
            int length = compBuf.readableBytes();
            byte[] array = new byte[length];
            compBuf.getBytes(0, array);
            System.out.println(array);
        }
    }

    private static void composeUseJDK() {
        // Compose legacy JDK ByteBuffer, 需要构造新数组,拷贝有性能问题
        ByteBuffer header = null;
        ByteBuffer body = null;
        // use an array to composite them
        ByteBuffer[] message = new ByteBuffer[]{header, body};
        // Use copy to merge both
        ByteBuffer message2 = ByteBuffer.allocate(header.remaining() + body.remaining());
        message2.put(header);
        message2.put(body);
        message2.flip();
    }

    private static void directBuf() {
        // access data
        ByteBuf directBuf = null;
        if (!directBuf.hasArray()) {
            int length = directBuf.readableBytes();
            byte[] array = new byte[length];
            directBuf.getBytes(0, array);// read bytes into array
            System.out.println(array);
        }
    }

    private static void heapBuf() {
        // access backing array
        ByteBuf heapBuf = null;
        if (heapBuf.hasArray()) {
            byte[] array = heapBuf.array();
            int offset = heapBuf.arrayOffset();// calculate offset of first byte in it
            int length = heapBuf.readableBytes();

            System.out.println("offset: " + offset + " " + length);
        }
    }
}
