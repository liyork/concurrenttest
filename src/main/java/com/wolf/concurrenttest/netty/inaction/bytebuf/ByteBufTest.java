package com.wolf.concurrenttest.netty.inaction.bytebuf;

import io.netty.buffer.*;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Description:相比较nio使用position，通过两个位置指针操作，互不影响
 * 在写入时检查容量，需要时动态扩容
 * 堆内存的bytebuffer进行socket的io操作时需要将内存复制到内核的channel中。堆外内存bytebuffer就不需要拷贝
 * 最佳实践：io通信线程的读写缓冲区用directbytebuf，后端业务消息编解码使用heapbytebuf
 * 基于对象池的bytebuf可以循环利用，提升内存的使用效率，降低由于高负载、大并发导致的频繁gc。
 * <p>
 * <p>
 * <br/> Created on 9/21/17 8:31 AM
 *
 * @author 李超
 * @since 1.0.0
 */
public class ByteBufTest {

    Charset charset = StandardCharsets.UTF_8;
    String str = "Netty in action rocks!";
    ByteBuf buf = Unpooled.copiedBuffer(str, charset);

    @Test
    public void testHeapBuf() throws UnsupportedEncodingException {
        getHeapBuf();
    }

    private ByteBuf getHeapBuf() {
        //UnpooledByteBufAllocator$InstrumentedUnpooledUnsafeHeapByteBuf(ridx: 0, widx: 12, cap: 256)
        ByteBuf byteBuf = Unpooled.buffer();
        byteBuf.writeInt(1);// 4byte
        byteBuf.writeInt(2);
        byteBuf.writeInt(3);
        if (byteBuf.hasArray()) {//InstrumentedUnpooledUnsafeHeapByteBuf.hasArray永远返回true
            byte[] array = byteBuf.array();
            int offset = byteBuf.arrayOffset();
            int length = byteBuf.readableBytes();
            System.out.println(offset + " " + length + " " + array.length);

            //for (int i = 0; i < length; i++) {
            //    System.out.println((char) byteBuf.getByte(i));//does not modify readerIndex or writerIndex of this buffer.
            //}
            System.out.println(byteBuf.getInt(0) + " " + byteBuf.getInt(4) + " " + byteBuf.getInt(8));
        }
        return byteBuf;
    }

    @Test
    public void testDirectBuf() {
        getDirectBuf();
    }

    private ByteBuf getDirectBuf() {
        // UnpooledByteBufAllocator$InstrumentedUnpooledUnsafeNoCleanerDirectByteBuf(ridx: 0, widx: 0, cap: 256)
        ByteBuf byteBuf = Unpooled.directBuffer();
        byteBuf.writeInt(1);
        byteBuf.writeInt(2);
        byteBuf.writeInt(3);
        System.out.println(byteBuf.hasArray());// InstrumentedUnpooledUnsafeNoCleanerDirectByteBuf.hasArray永远返回false

        byte[] array = new byte[byteBuf.readableBytes()];
        byteBuf.getBytes(0, array);
        System.out.println(array);
        return byteBuf;
    }

    @Test
    public void testCompositeBuf() {
        CompositeByteBuf byteBuf = Unpooled.compositeBuffer();
        byteBuf.addComponent(getHeapBuf());
        byteBuf.addComponent(getDirectBuf());
        System.out.println(byteBuf.hasArray());// CompositeByteBuf.hasArray依据components.size()

        for (Iterator<ByteBuf> it = byteBuf.iterator(); it.hasNext(); ) {
            Object o = it.next();
            System.out.println(o);
        }
    }

    //if you need to free up memory as soon as possible.Such an operation isn t free and may affect performance
    //discardReadBytes由于有数组拷贝，是牺牲性能时间换取空间。如果面临需要扩容时也会拷贝+空间，所以那时使用这个合适一些，减少了空间
    //writerIndex - readerIndex = length,writeindex位于准备写入的位置
    // The components at positions srcPos through srcPos+length-1 in the source array
    @Test
    public void testDiscardReadBytes() throws UnsupportedEncodingException {
        ByteBuf byteBuf = Unpooled.buffer();
        byteBuf.writeInt(11);
        byteBuf.writeInt(22);
        byteBuf.writeInt(33);
        byteBuf.writeInt(44);

        System.out.println(byteBuf.readInt());
        System.out.println(byteBuf.readInt());

        System.out.println(byteBuf.arrayOffset() + " " + byteBuf.readerIndex() + " " + byteBuf.writerIndex());

        byteBuf.discardReadBytes();//将剩余的字节拷贝前移

        System.out.println(byteBuf.arrayOffset() + " " + byteBuf.readerIndex() + " " + byteBuf.writerIndex());
    }

    // readerIndex = writerIndex = 0
    @Test
    public void testClear() throws UnsupportedEncodingException {
        ByteBuf byteBuf = Unpooled.buffer();
        byteBuf.writeInt(1);
        byteBuf.writeInt(2);
        byteBuf.writeInt(3);
        byteBuf.writeInt(4);

        System.out.println(byteBuf.readInt());
        System.out.println(byteBuf.readInt());

        System.out.println(byteBuf.arrayOffset() + " " + byteBuf.readerIndex() + " " + byteBuf.writerIndex());

        byteBuf.clear();

        System.out.println(byteBuf.arrayOffset() + " " + byteBuf.readerIndex() + " " + byteBuf.writerIndex());
    }

    @Test
    public void testMarkAndReset() throws UnsupportedEncodingException {
        ByteBuf byteBuf = Unpooled.buffer();
        byteBuf.writeInt(1);
        byteBuf.writeInt(2);
        byteBuf.writeInt(3);
        byteBuf.writeInt(4);

        System.out.println(byteBuf.readInt());
        System.out.println(byteBuf.readInt());
        byteBuf.markReaderIndex();
        System.out.println(byteBuf.arrayOffset() + " " + byteBuf.readerIndex() + " " + byteBuf.writerIndex());
        System.out.println(byteBuf.readInt());
        System.out.println(byteBuf.arrayOffset() + " " + byteBuf.readerIndex() + " " + byteBuf.writerIndex());
        byteBuf.resetReaderIndex();
        System.out.println(byteBuf.arrayOffset() + " " + byteBuf.readerIndex() + " " + byteBuf.writerIndex());
    }

    //get、set不移动index
    @Test
    public void testSetGet() throws UnsupportedEncodingException {
        ByteBuf byteBuf = Unpooled.buffer(16);
        byteBuf.writeInt(2);
        byteBuf.writeInt(4);
        byteBuf.writeInt(1);

        System.out.println(byteBuf.getInt(0));
        byteBuf.setInt(0, 3);

        System.out.println(byteBuf.readerIndex() + " " + byteBuf.writerIndex());
    }

    //read、write移动index
    @Test
    public void testReadWrite() throws UnsupportedEncodingException {
        ByteBuf byteBuf = Unpooled.buffer(16);
        Random random = new Random();
        while (byteBuf.writableBytes() >= 4) {
            byteBuf.writeInt(random.nextInt(10));
        }
        byteBuf.writeInt(44);// 可自动扩容，不过涉及数据拷贝

        while (byteBuf.isReadable()) {
            System.out.println(byteBuf.readInt());
        }
    }

    // 定位元素
    @Test
    public void testByteBefore() throws UnsupportedEncodingException {
        ByteBuf byteBuf = Unpooled.buffer();
        byteBuf.writeInt(1);
        byteBuf.writeInt(2);
        byteBuf.writeByte(44);
        byteBuf.writeInt(3);
        byteBuf.writeInt(4);

        byteBuf.readInt();

        // 从int fromIndex, int toIndex找value
        int i1 = byteBuf.indexOf(0, byteBuf.capacity(), (byte) 3);
        System.out.println(i1);
        System.out.println(byteBuf.arrayOffset() + " " + byteBuf.readerIndex() + " " + byteBuf.writerIndex());
        int i = byteBuf.bytesBefore((byte) 3);// 从readerindex的位置开始计算查找3
        System.out.println(i);

        System.out.println(byteBuf.arrayOffset() + " " + byteBuf.readerIndex() + " " + byteBuf.writerIndex());
    }

    // slice a ByteBuf
    // 底层引用一个，create a view of an existing buffer
    @Test
    public void testSlice() throws UnsupportedEncodingException {
        System.out.println(str.length());// 22
        ByteBuf byteBuf = Unpooled.copiedBuffer(str, charset);
        System.out.println(byteBuf);// 0, 22 ,66
        ByteBuf slice = byteBuf.slice(0, 14);// share the same content
        System.out.println(slice.toString(charset));

        slice.setByte(0, 'J');
        System.out.println((char) slice.getByte(0));
        System.out.println(slice.getByte(0) == byteBuf.getByte(0));// 都变了
    }

    // user a slice whenever possible, and use copy only as needed
    // creating a copy of the ByteBuf is more expensive, it needs to do a memory copy
    //两个对象,修改之一互不影响
    @Test
    public void testCopy() throws UnsupportedEncodingException {
        ByteBuf copy = buf.copy(0, 14);
        System.out.println(copy.toString(charset));

        copy.setByte(0, 'J');
        System.out.println(copy.getByte(0) != buf.getByte(0));
    }

    @Test
    public void testGetAndSet() throws UnsupportedEncodingException {
        System.out.println((char) buf.getByte(0));

        int readerIndex = buf.readerIndex();
        int writerIndex = buf.writerIndex();

        buf.setByte(0, 'B');
        System.out.println((char) buf.getByte(0));

        assert readerIndex == buf.readerIndex();
        assert writerIndex == buf.writerIndex();
    }

    @Test
    public void testReadAndWrite() throws UnsupportedEncodingException {
        System.out.println((char) buf.readByte());

        int readerIndex = buf.readerIndex();
        int writerIndex = buf.writerIndex();

        buf.writeByte((byte) '?');

        assert readerIndex == buf.readerIndex();
        assert writerIndex != buf.writerIndex();
    }

    // a message object that stores its payload/data in a ByteBuf
    // todo 如何实现response功能？内部就是多了一个data引用
    @Test
    public void testByteBufHolder() throws UnsupportedEncodingException {
        System.out.println(buf);
        ByteBufHolder byteBufHolder = new DefaultByteBufHolder(buf);
        byteBufHolder.touch("a");// 这里底层返回了this，没有啥用。
        byteBufHolder.touch(1);
        System.out.println(buf);

        ByteBuf content = byteBufHolder.content();
        System.out.println(content);
    }

    // Unpooled class makes it easier to user the netty's buffer API outside netty

    // ByteBufAllocator allocator = channel.alloc()
    // ByteBufAllocator allocator = ctx.alloc()
    @Test
    public void testByteBufAllocator() throws UnsupportedEncodingException {
        PooledByteBufAllocator pooledByteBufAllocator = new PooledByteBufAllocator();
        ByteBuf b1 = pooledByteBufAllocator.buffer();// return ByteBuf may be of type heap or direct depend the implementation
        ByteBuf b2 = pooledByteBufAllocator.directBuffer();
        System.out.println(b1 + "__" + b2);
        UnpooledByteBufAllocator unpooledByteBufAllocator = new UnpooledByteBufAllocator(true);
        ByteBuf b3 = unpooledByteBufAllocator.buffer();
        ByteBuf b4 = unpooledByteBufAllocator.directBuffer();
        System.out.println(b3 + "__" + b4);


        Channel channel = new NioSocketChannel();
        ByteBufAllocator allocator = channel.alloc();
        System.out.println(allocator);

        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast(new DiscardOutboundHandler());
        ChannelHandlerContext ctx = pipeline.context("DiscardOutboundHandler#0");
        ByteBufAllocator allocator2 = ctx.alloc();
        System.out.println(allocator2);
    }

    // hex string is much more user friendly
    @Test
    public void testByteBufUtil() throws UnsupportedEncodingException {
        String s = ByteBufUtil.hexDump(buf);
        System.out.println(s);
    }

    @Test
    public void testChannelPipeline() throws UnsupportedEncodingException {
        Channel channel = new NioServerSocketChannel();
        ChannelPipeline pipeline = channel.pipeline();// DefaultChannelPipeline伴随channel而创建

        ChannelHandler channelHandler = new SimpleChannelInboundHandler<String>() {
            @Override
            protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
            }
        };
        ChannelHandler channelHandler2 = new SimpleChannelInboundHandler<String>() {
            @Override
            protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
            }
        };
        pipeline.addFirst(channelHandler);
        pipeline.addLast(channelHandler2);

        pipeline.get("xx");

        ChannelHandlerContext context = pipeline.context(channelHandler);
        System.out.println("context:" + context);

        List<String> names = pipeline.names();
        System.out.println("names:" + names);

        Iterator<Map.Entry<String, ChannelHandler>> iterator = pipeline.iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }

        pipeline.fireChannelRegistered();//invoke next
    }

    //写事件流动
    @Test
    public void testPassWriteEvent() throws UnsupportedEncodingException {
        ChannelHandlerContext ctx = null;
        Channel channel = ctx.channel();
        ByteBuf bytebuf = Unpooled.copiedBuffer("xxxx", charset);
        channel.write(bytebuf);
        // 与上面的操作相同
        ChannelPipeline pipeline = ctx.pipeline();
        pipeline.write(bytebuf);


        //获取指定的ctx,调用write将触发下一个handler
        ChannelHandlerContext context = pipeline.context("appointhandlername");
        context.write(bytebuf);

        //一个handler可以被多个pipeline使用，如果是那样小心线程问题
    }

    //ChannelHandlerAdapter is Skelton implementation of a {@link ChannelHandler}

    //自动扩容
    @Test
    public void testExpandBuffer() throws Exception {
        ByteBuf byteBuf = Unpooled.buffer(2);

        for (int i = 0; i < 10000000; i++) {
            byteBuf.writeInt(2);
        }
    }

    //先翻倍再步进
    @Test
    public void testNewCapacity() throws Exception {
        Random random = new Random();
        int threshold = 4;
//        for (int i = 0; i < 100; i++) {
//            float v = random.nextFloat()*10;
//            System.out.println("current v :"+v);
//            //抹去除以threshold后的小数部分，即几倍于threshold再乘以threshold
//            int newCapacity = (int) (v / threshold) * threshold;//求threshold倍数
//            System.out.println("new capacity : "+newCapacity);
//        }

        for (int i = 0; i < 100; i++) {
            float v = random.nextFloat() * 10;
            System.out.println("current v :" + v);
            //float除乘不会抹小数，最后int抹去
            int newCapacity = (int) (v / threshold * threshold);
            System.out.println("new capacity : " + newCapacity);
        }


        //int除法，不满足整数的被忽略，也就是小于threshold的部分被舍去。
        //抹除小于threshold的部分，其实也是求threshold的倍数
//        for (int i = 0; i < 100; i++) {
//            int v = random.nextInt(1000);
//            System.out.println("current v :"+v);
//            int newCapacity = v / threshold * threshold;//求threshold倍数
//            System.out.println("new capacity : "+newCapacity);
//        }

        //求：最接近minNewCapacity的threshold倍数值
//        int newCapacity = minNewCapacity / threshold * threshold;
    }

    @Test
    public void testRefCount() throws Exception {
        ByteBuf byteBuf = Unpooled.buffer(2);

        // Increases the reference count by 1
        byteBuf.retain();

        byteBuf.writeInt(2);
        byteBuf.writeInt(3);

        System.out.println(byteBuf.refCnt());

        // Decreases the reference count by 1 and deallocates this object if the reference count reaches at 0
        // getAndAdd(-1),返回0则true
        boolean release = byteBuf.release();
        System.out.println(release);
        System.out.println(byteBuf.refCnt());

        release = byteBuf.release();
        System.out.println(release);
        System.out.println(byteBuf.refCnt());

        System.out.println(byteBuf.readerIndex() + " " + byteBuf.writerIndex());
        byteBuf.readInt();//异常IllegalReferenceCountException: refCnt: 0s,被释放成0后就不能再用了
    }

    @Test
    public void testConvert2NioBuffer() throws Exception {
        ByteBuf byteBuf = Unpooled.buffer(2);

        byteBuf.writeInt(2);
        byteBuf.writeInt(3);

        int i1 = byteBuf.readInt();
        System.out.println(i1);

        System.out.println(byteBuf.readerIndex() + " " + byteBuf.writerIndex());

        ByteBuffer byteBuffer = byteBuf.nioBuffer();
        System.out.println(byteBuffer.position() + " " + byteBuffer.limit());
    }

    @Test
    public void testSimpleUse() {
        ByteBuf buf = Unpooled.buffer(10);
        System.out.println("buf.toString====================>" + buf.toString());
        System.out.println("ByteBuf中的内容为===============>" + Arrays.toString(buf.array()) + "\n");

        byte[] bytes = {1, 2, 3, 4, 5};
        buf.writeBytes(bytes);
        System.out.println("写入内容后buf.toString===========>" + buf);
        System.out.println("ByteBuf中的内容为===============>" + Arrays.toString(buf.array()) + "\n");

        byte b1 = buf.readByte();
        byte b2 = buf.readByte();
        System.out.println("读取的bytes为====================>" + Arrays.toString(new byte[]{b1, b2}));
        System.out.println("读取一段内容后buf.toString===========>" + buf);
        System.out.println("ByteBuf中的内容为===============>" + Arrays.toString(buf.array()) + "\n");

        buf.discardReadBytes();
        System.out.println("将读取的内容丢弃后buf.toString========>" + buf);// 未读的不被丢弃
        System.out.println("ByteBuf中的内容为===============>" + Arrays.toString(buf.array()) + "\n");

        // 只清指针
        buf.clear();
        System.out.println("clear后buf.toString==========>" + buf);
        System.out.println("ByteBuf中的内容为===============>" + Arrays.toString(buf.array()) + "\n");

        byte[] bytes2 = {1, 2, 3};
        buf.writeBytes(bytes2);
        System.out.println("再写入的bytes====================>" + Arrays.toString(bytes2));
        System.out.println("写入一段内容后buf.toString===========>" + buf);
        System.out.println("ByteBuf中的内容为===============>" + Arrays.toString(buf.array()) + "\n");

        // 从index位置清理len数据为0,ridx,widx不变
        buf.setZero(0, buf.capacity());
        System.out.println("将内容清零后buf.toString==============>" + buf);
        System.out.println("ByteBuf中的内容为================>" + Arrays.toString(buf.array()) + "\n");

        // 扩容
        byte[] bytes3 = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
        buf.writeBytes(bytes3);// 从widx开始写入
        System.out.println("再写入的bytes为====================>" + Arrays.toString(bytes3));
        System.out.println("写入一段内容后buf.toString===========>" + buf);
        System.out.println("8.ByteBuf中的内容为===============>" + Arrays.toString(buf.array()) + "\n");
    }
}
