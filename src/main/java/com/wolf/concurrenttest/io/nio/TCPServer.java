package com.wolf.concurrenttest.io.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p> Description:
 * Java自1.4以后,加入了新IO特性,NIO. 号称new IO. NIO带来了non-blocking特性.
 * 这篇文章主要讲的是如何使用NIO的网络新特性,来构建高性能非阻塞并发服务器.
 * <p/>
 * 在NIO之前
 * 服务器还是在使用阻塞式的java socket. 以Tomcat最新版本没有开启NIO模式的源码为例,
 * tomcat会accept出来一个socket连接,然后调用processSocket方法来处理socket.
 * <p/>
 * while(true) {
 * ....
 * Socket socket = null;
 * try {
 * // Accept the next incoming connection from the server
 * // socket
 * socket = serverSocketFactory.acceptSocket(serverSocket);
 * }
 * ...
 * ...
 * // Configure the socket
 * if (running && !paused && setSocketOptions(socket)) {
 * // Hand this socket off to an appropriate processor
 * if (!processSocket(socket)) {
 * countDownConnection();
 * // Close socket right away(socket);
 * closeSocket(socket);
 * }
 * }
 * 使用ServerSocket.accept()方法来创建一个连接. accept方法是阻塞方法,在下一个connection进来之前,accept会阻塞.
 * <p/>
 * 在一个socket进来之后,Tomcat会在thread pool里面拿出一个thread来处理连接的socket. 然后自己快速的脱身去接受下一个socket连接. 代码如下:
 * <p/>
 * <p/>
 * protected boolean processSocket(Socket socket) {
 * // Process the request from this socket
 * try {
 * SocketWrapper<Socket> wrapper = new SocketWrapper<Socket>(socket);
 * wrapper.setKeepAliveLeft(getMaxKeepAliveRequests());
 * // During shutdown, executor may be null - avoid NPE
 * if (!running) {
 * return false;
 * }
 * getExecutor().execute(new SocketProcessor(wrapper));
 * } catch (RejectedExecutionException x) {
 * log.warn("Socket processing request was rejected for:"+socket,x);
 * return false;
 * } catch (Throwable t) {
 * ExceptionUtils.handleThrowable(t);
 * // This means we got an OOM or similar creating a thread, or that
 * // the pool and its queue are full
 * log.error(sm.getString("endpoint.process.fail"), t);
 * return false;
 * }
 * return true;
 * }
 * 而每个处理socket的线程,也总是会阻塞在while(true) sockek.getInputStream().read() 方法上.
 * <p/>
 * 总结就是, 一个socket必须使用一个线程来处理. 致使服务器需要维护比较多的线程. 线程本身就是一个消耗资源的东西,
 * 并且每个处理socket的线程都会阻塞在read方法上,使得系统大量资源被浪费.
 * <p/>
 * 以上这种socket的服务方式适用于HTTP服务器,每个http请求都是短期的,无状态的,并且http后台的业务逻辑也一般比较复杂. 使用多线程和阻塞方式是合适的.
 * <p/>
 * 倘若是做游戏服务器,尤其是CS架构的游戏.这种传统模式服务器毫无胜算.游戏有以下几个特点是传统服务器不能胜任的:
 * 1, 持久TCP连接. 每一个client和server之间都存在一个持久的连接.当CCU(并发用户数量)上升,阻塞式服务器无法为每一个连接运行一个线程.
 * 2, 自己开发的二进制流传输协议. 游戏服务器讲究响应快.那网络传输也要节省时间. HTTP协议的冗余内容太多,一个好的游戏服务器传输协议,可以使得message压缩到3-6倍甚至以上.这就使得游戏服务器要开发自己的协议解析器.
 * 3, 传输双向,且消息传输频率高.假设一个游戏服务器instance连接了2000个client,每个client平均每秒钟传输1-10个message,
 * 		一个message大约几百字节或者几千字节.而server也需要向client广播其他玩家的当前信息.这使得服务器需要有高速处理消息的能力.
 * 4, CS架构的游戏服务器端的逻辑并不像APP服务器端的逻辑那么复杂. 网络游戏在client端处理了大部分逻辑,server端负责简单逻辑,甚至只是传递消息.
 * <p/>
 * 在Java NIO出现以后
 * <p/>
 * 出现了使用NIO写的非阻塞网络引擎,比如Apache Mina, JBoss Netty, Smartfoxserver BitSwarm. 比较起来, Mina的性能不如后两者.
 * Tomcat也存在NIO模式,不过需要人工开启.
 * <p/>
 * 首先要说明一下, 与App Server的servlet开发模式不一样, 在Mina, Netty和BitSwarm上开发应用程序都是Event Driven的设计模式.
 * Server端会收到Client端的event,Client也会收到Server端的event,Server端与Client端的都要注册各种event的EventHandler来handle event.
 * <p/>
 * 用大白话来解释NIO:
 * 1, Buffers, 网络传输字节存放的地方.无论是从channel中取,还是向channel中写,都必须以Buffers作为中间存贮格式.
 * 2, Socket Channels. Channel是网络连接和buffer之间的数据通道.每个连接一个channel.就像之前的socket的stream一样.
 * 3, Selector. 像一个巡警,在一个片区里面不停的巡逻. 一旦发现事件发生,立刻将事件select出来.不过这些事件必须是提前注册在selector上的. select出来的事件打包成SelectionKey.里面包含了事件的发生事件,地点,人物. 如果警察不巡逻,每个街道(socket)分配一个警察(thread),那么一个片区有几条街道,就需要几个警察.但现在警察巡逻了,一个巡警(selector)可以管理所有的片区里面的街道(socketchannel).
 * <p/>
 * 以上把警察比作线程,街道比作socket或socketchannel,街道上发生的一切比作stream.把巡警比作selector,引起巡警注意的事件比作selectionKey.
 * <p/>
 * 从上可以看出,使用NIO可以使用一个线程,就能维护多个持久TCP连接.
 * <p/>
 * NIO实例
 * <p/>
 * 下面给出NIO编写的EchoServer和Client. Client连接server以后,将发送一条消息给server. Server会原封不懂的把消息发送回来.Client再把消息发送回去.Server再发回来.用不休止. 在性能的允许下,Client可以启动任意多.
 * <p/>
 * 以下Code涵盖了NIO里面最常用的方法和连接断开诊断.注释也全.
 * <p/>
 * 首先是Server的实现. Server端启动了2个线程,connectionBell线程用于巡逻新的连接事件. readBell线程用于读取所有channel的数据. 注解: Mina采取了同样的做法,只是readBell线程启动的个数等于处理器个数+1. 由此可见,NIO只需要少量的几个线程就可以维持非常多的并发持久连接.
 * <p/>
 * 每当事件发生,会调用dispatch方法去处理event. 一般情况,会使用一个ThreadPool来处理event. ThreadPool的大小可以自定义.但不是越大越好.如果处理event的逻辑比较复杂,比如需要额外网络连接或者复杂数据库查询,那ThreadPool就需要稍微大些.(猜测)Smartfoxserver处理上万的并发,也只用到了3-4个线程来dispatch event.
 * <p/>
 * 重要：作为服务端，如果什么操作都要依赖于客户端，很多操作都阻塞
 * 对客户端过来的request内容，也是非阻塞（这里是不必苦苦等待其数据的到来），
 * 都是不必一直眼巴巴的看着那个连接，那些数据，而是如果有我关心的事件了，我再进行处理
 * <p/>
 * 堵塞socket就是在accept、read、write等IO操作的的时候，如果没有可用符合条件的资源，不马上返回，一直等待直到有资源为止
 * Selector允许单线程处理多个 Channel。如果你的应用打开了多个连接（通道），但每个连接的流量都很低，使用Selector就会很方便。例如，在一个聊天服务器中。
 * 创建、维护和切换线程需要的系统开销
 * 增加线程，大部分的时间里客户端是处于空闲状态的
 * 多路复用I/O的基本做法是由一个线程来管理多个套接字连接
 * 不过一个请求一个线程的处理模型并不是很理想。原因在于耗费时间创建的线程，在大部分时间可能处于等待的状态
 * Date: 2015/10/23
 * Time: 13:37
 *
 * @author 李超
 * @version 1.0
 * @since 1.0
 */
public class TCPServer {
	// 缓冲区大小
	private static final int bufferSize = 1024;

	// 超时时间，单位毫秒
	private static final int timeOut = 3000;

	// 本地监听端口
	private static final int listenPort = 1978;

	private static AtomicInteger atomicInteger = new AtomicInteger(1);

	public static void main(String[] args) throws IOException {
		// 创建选择器
		Selector selector = Selector.open();

		// 打开监听信道
		ServerSocketChannel listenerChannel = ServerSocketChannel.open();

		// 与本地端口绑定
		listenerChannel.socket().bind(new InetSocketAddress(listenPort));

		// 设置为非阻塞模式
		listenerChannel.configureBlocking(false);

		// 将选择器绑定到监听信道,只有非阻塞信道才可以注册选择器.并在注册过程中指出该信道可以进行Accept操作
		listenerChannel.register(selector, SelectionKey.OP_ACCEPT);

		// 创建一个处理协议的实现类,由它来具体操作
		TCPProtocol protocol = new TCPProtocolImpl(bufferSize, atomicInteger);

		// 反复循环,等待IO
		while (true) {
			// 等待某信道就绪(或超时)
			if (selector.select(timeOut) == 0) {
				System.out.print("独自等待.");
				continue;
			}

			// 取得迭代器.selectedKeys()中包含了每个准备好某一I/O操作的信道的SelectionKey
			Iterator<SelectionKey> keyIter = selector.selectedKeys().iterator();

			while (keyIter.hasNext()) {
				SelectionKey key = keyIter.next();

				try {
					//接受链接
					if (key.isValid() && key.isAcceptable()) {
						System.out.println("isAcceptable");
						// 有客户端连接请求时
						protocol.handleAccept(key);
					}

					//读取
					if (key.isValid() && key.isReadable()) {
						System.out.println("isReadable");
						// 从客户端读取数据
						protocol.handleRead(key);
					}

					//服务端一般不需要这个
//					if(key.isValid() && key.isWritable()){
//						System.out.println("isWritable");
//						// 客户端可写时
//						protocol.handleWrite(key);
//					}
				} catch (Exception ex) {
					//log 或者再处理几次后不管
					System.out.println(ex.getMessage());
				} finally {
					// 移除处理过的键
					keyIter.remove();
				}

			}
		}
	}
}
