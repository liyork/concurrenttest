package com.wolf.concurrenttest.io.aio;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Description:异步aio，当io操作完成之后而不是像nio操作准备好回调指定函数
 * <br/> Created on 24/03/2018 3:57 AM
 *
 * @author 李超
 * @since 1.0.0
 */
public class AioEchoServer {

    public final static int PORT = 8000;
    private AsynchronousServerSocketChannel server;

    public AioEchoServer() throws IOException {
        server = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(PORT));
    }

    public void start() {
        System.out.println("Server listen on " + PORT);

        //立即返回，io操作完后调用CompletionHandler
        CompletionHandler<AsynchronousSocketChannel, Object> acceptHandler = new CompletionHandler<AsynchronousSocketChannel, Object>() {
            ByteBuffer buffer = ByteBuffer.allocate(1024);

            //被执行则已经有客户端成功连接
            @Override
            public void completed(AsynchronousSocketChannel result, Object attachment) {
                System.out.println(Thread.currentThread().getName() + " completed");
                Future<Integer> writeResult = null;
                try {
                    buffer.clear();
                    Future<Integer> future = result.read(buffer);//立即返回
                    future.get(100, TimeUnit.SECONDS);//变成同步

                    //哦，发现问题了，一直还说，怎么这么多0呢后面，没办法使用byte解决吧，但是呢出现异常，debug才发现原来没有flip
//                    byte []by = new byte[1024];
//                    int i = 0;
//                    while (buffer.hasRemaining()) {
//                        byte b = buffer.get();
//                        if (b == 0) {
//                            break;
//                        }
//                        by[i++] = b;
//                    }
//                    System.out.println("server receive ,msg:" +
//                            new String(by, 0, --i, "utf-8"));

                    buffer.flip();//若注释掉次行，则下面从0--1018直接字符串。。。

                    int remaining = buffer.remaining();
                    System.out.println("remaining:" + remaining);
                    System.out.println("server receive ,remaining:" + remaining + ",msg:" +
                            new String(buffer.array(), 0, remaining, "utf-8"));

//                    buffer.flip();//不能两次flip...
                    writeResult = result.write(buffer);//立即返回
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        server.accept(null, this);
                        writeResult.get();//确保之前write操作完成
                        result.close();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                System.out.println("failed:" + exc);
                exc.printStackTrace();
            }
        };

        server.accept(null, acceptHandler);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        new AioEchoServer().start();
        while (true) {
            Thread.sleep(1000);
        }
    }
}
