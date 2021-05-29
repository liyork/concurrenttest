package com.wolf.concurrenttest.io.aio;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * Description:
 * <br/> Created on 24/03/2018 4:10 AM
 *
 * @author 李超
 * @since 1.0.0
 */
public class AioEchoClient {

    public static void main(String[] args) throws IOException, InterruptedException {

        AsynchronousSocketChannel client = AsynchronousSocketChannel.open();

        CompletionHandler<Void, Object> connectHandler = new CompletionHandler<Void, Object>() {
            @Override
            public void completed(Void result, Object attachment) {
                System.out.println(" connectHandler completed...");
                try {

                    CompletionHandler<Integer, Object> writeHandler = new CompletionHandler<Integer, Object>() {
                        @Override
                        public void completed(Integer result, Object attachment) {
                            System.out.println(" writeHandler completed...");
                            try {

                                ByteBuffer buffer = ByteBuffer.allocate(1024);
                                CompletionHandler<Integer, ByteBuffer> readHandler = new CompletionHandler<Integer, ByteBuffer>() {
                                    @Override
                                    public void completed(Integer result, ByteBuffer attachment) {
                                        System.out.println(" readHandler completed...");

                                        buffer.flip();
                                        try {
                                            int remaining = buffer.remaining();
                                            System.out.println("remaining:"+remaining);
                                            System.out.println("readHandler:"+new String(buffer.array(),0,remaining,"utf-8"));
                                        } catch (UnsupportedEncodingException e) {
                                            e.printStackTrace();
                                        }
                                        try {
                                            client.close();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void failed(Throwable exc, ByteBuffer attachment) {
                                        System.out.println("failed 1...");
                                        exc.printStackTrace();
                                    }
                                };

                                client.read(buffer, buffer, readHandler);//异步
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void failed(Throwable exc, Object attachment) {
                            System.out.println("failed 2...");
                            exc.printStackTrace();
                        }
                    };

                    client.write(ByteBuffer.wrap("Hello!".getBytes("utf-8")), null, writeHandler);//异步
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                System.out.println("failed 3...");
                exc.printStackTrace();
            }
        };

        client.connect(new InetSocketAddress("localhost", 8000), null, connectHandler);//异步

        Thread.sleep(1000);
    }
}
