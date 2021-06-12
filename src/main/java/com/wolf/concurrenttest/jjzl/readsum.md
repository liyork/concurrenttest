netty进阶之路_跟着案例学netty

## 案例1异常退出
知识都是相互关联的，很难在基础知识不扎实的情况下掌握更高阶的知识

linux常用信号量
SIGKILL  终止进程，强制杀死进程
SIGTERM  终止进程，软件终止信号
SIGTSTP  终止进程，终端来的停止信号
SIGUSR1  终止进程，用户定义信号1
SIGUSR2  终止进程，用户定义信号2
SIGINT  终止进程，中断进程
SIGQUIT  建立core文件终止进程，并且生成core文件


netty优雅退出三大类操作：
1.把NIO线程的状态位设置ST_SHUTTING_DOWN，不再处理新的消息
2.推出前的预处理操作：把发送队列中尚未发送或者正在发送的消息发送完、把已经到期或在退出超期之前到期的定时任务执行完成、把用户注册到NIO线程的退出Hook任务执行完
3.资源的释放操作：所有Channel的释放、多路复用器的去注册和关闭、所有队列和定时任务的清空取消，eventloop线程的退出

NioEventLoopGroup是NioEventLoop线程组，循环EventLoop数组并调用shutdownGracefully
NioEventLoop的shutdownGracefully中，先修改线程状态为正在关闭状态。然后closeAll将注册在selector上的所有Channel都关闭，调用ChannelUnsafe.close
AbstractUnsafe.close完成几个功能：
a.判断当前链路是否有消息正在发送，若有则将selectionKey的去注册操作封装成Task放到eventLoop中
b.将发送队列清空，不在允许发送新的消息
c.NioSocketChannel.doClose
d.pipeline.fireChannelInactive
e.调用AbstractNioChannel.Deregister从多路复用器上取消selectionKey
f.ChannelOutboundBuffer.close，释放发送队列中所有尚未完成发送的ByteBuf，等待GC

TaskQueu退出处理流程
NioEventLoop执行完closeAll，需要调用confirmShutdown看是否真的可以退出
a.执行尚在TaskQueue中排队的task
b.执行注册到NioEventLoop中的shutdownHook
c.判断是否到达优雅退出的指定超时时间，若到达则立即退出
d.若没到时间，暂不退出，每隔100ms检测是否有新的任务加入，有则继续执行
当confirmShutdown返回true，则NioEventloop线程退出

netty优雅退出更重要的是保证资源、句柄和线程的快速释放，以及相关对象的清理。
应用程序需要配合做好容错设计和处理。

netty优雅退出常用于进程退出时，在应用的shutdownHook中调用EventloopGroup.shutdownGracefully，指定超时时间


## netty客户端连接池资源泄露
连接池的数量配置100，业务高峰发生OOM，GC overhead limit exceeded
线程堆栈发现大量EventLoopGroup线程池
导致内存泄漏的原因是创建了大量的EventLoopGroup线程池，每个EventLoopGroup线程池中的NioEventLoop线程对应一个TCP连接。当压力大、连接数多就导致膨胀
由于采用BIO模式来调用NIO通信框架。
netty解决了传统BIO模型的问题，NIO线程中聚合了多路复用器selector，会不断地轮询注册在其上的Channel，若某个Channel上有新的TCP连接接入、读和写操作，这个
Channel处于就绪状态，会被Selector轮询出来，然后通过SelectionKey可以获取就绪Channel集合，进行后续的I/O操作。
# netstat -an|find "ESTABLISHED"|find "18081"


javanio客户端创建原理：
1.打开SocketChannel，绑定客户端本地地址
2.设置SocketChannel为非阻塞模式，设置客户端连接的TCP参数
3.异步连接服务端
4.判断是否连接成功，若是则直接注册读状态位到多路复用器，否则注册连接位
5.向Reactor线程的多路复用器注册OP_CONNECT事件，监听服务端的TCP ACK应答
6.创建Reactor线程，创建多路复用器并启动线程
7.多路复用器在线程run中无限轮询准备就绪的key
8.接收Connect事件进行处理
9.判断连接结果，若成功，注册读事件到多路复用器
10.异步读客户端请求消息到缓冲区
11.对ByteBuffer编解码，如有半包消息接收缓冲区Reset，继续读取后续的报文，将解码成功的消息封装成Task放入业务线程池。
12.将POJO对象encode成ByteBuffer，调用SocketChannel.write将消息异步发送给客户端


netty客户端创建原理
1.创建Bootstrap实例，设定参数，异步发起客户端连接
2.创建处理客户端连接、I/O读写的Reactor线程组NioEventLoopGroup，通过构造函数指定I/O线程的个数
3.通过Bootstrap的ChannelFactory和用户指定的Channel类型创建用于客户端连接的NioSocketChannel
4.创建默认ChannelPipeline，用于调度和执行网络事件
5.异步发起TCP客户端连接，若连接成功则将NioSocketChannel注册到多路复用器上，监听读操作位，用于数据报读取和消息发送，若没有立即连接成功，则
注册连接监听位到多路复用器，等待连接结果
6.注册对应的网络监听状态位到多路复用器
7.多路复用器在I/O线程轮询各Channel，处理链接结果
8.若连接成功，设置Future结果，发送连接成功事件，触发ChannelPipeline执行。
9.由ChannelPipeline调度执行系统和用户的ChannelHandler执行逻辑


Netty提供的主要TCP参数：
1.SO_TIMEOUT:控制读操作将阻塞多少毫秒。即阻塞读时一直没有数据，持续多少毫秒阻塞
2.SO_SNDBUF:套接字使用的发送缓冲区大小
3.SO_RCVBUF:套接字使用的接收缓冲区大小
4.SO_REUSEADDR:当网络上仍有数据向旧的ServerSocket传输数据时，是否允许新的ServerSocket绑定到与旧的ServerSocket同样的端口上。
5.CONNECT_TIMEOUT_MILLIS:客户端连接超时时间。NIO原生客户端不提供此功能，netty采用自定义连接超时定时器负责超时控制。
6.TCP_NODELAY:决定是否使用Nagle算法。若是时延敏感型应用建议关闭Nagle算法


为简化ChannelHandler的编排，Bottstrap提供ChannelInitializer，TCP链路注册成功后，调用initChannel接口，设置用户ChannelHandler。
1.执行客户端初始化时提供的类的initChannel方法，将应用自定义的ChannelHandler添加到ChannelPipeline中
2.将Bootstrap注册到ChannelPipeline用于初始化应用ChannelHandler的ChannelInitializer删除掉



## netty内存池泄露
问题：性能测试时，运行一段时间发现内存分配异常，服务端无法接收请求消息。
日志出现异常，内存上升，怀疑泄露
OutOfDirectMemoryError: failed to allocate xxxxxbyte(s) of direct memory

对业务ByteBuf申请的相关代码排查，发信啊响应消息由业务线程创建，但没有主动释放,怀疑可能导致内存泄露。
响应消息使用的PooledHeapByteBuf，但从内存占用趋势，并没有发现堆内存泄露
对内存快照，查看PooledUnsafeHeapByteBuf实例个数和内存占用很少，排除内存泄露嫌疑

对netty源码分析，ctx.writeAndFlush(respMsg)时，当消息发送完成，netty会主动释放内存，两种场景：
1.若是堆内存(PooledHeapByteBuf)，将HeapByteBuffer转换成DirectByteBuffer，并释放PooledHeapByteBuf到内存池，(AbstractNioChannel.newDirectBuffer)
若消息完整地写到SocketChannel中，则释放DirectByteBuffer,(ChannelOutboundBuffer.remove)
2.若是DirectByteBuffer则不用转换，消息发送完成后，由ChannelOutBoundBuffer.remove负责释放

如上可以确认不是响应消息没有主动释放导致的内存内泄漏，
进一步dump内存分析，
jmap -dump:format=b,file=router_memory.hprof pid
通过MemoryAnalyzer分析，内存泄漏点是Netty内存池对象PoolChunk，但由于请求和响应消息内存分配都来自PoolChunk，暂不确认是请求还是响应导致的问题。
进一步分析，发现响应消息用的是堆内存HeapByteBuffer，请求消息用的是DirectByteBuffer。
而dump出来的是堆内存，若是堆内存泄漏，dump出来的内存文件应该包含大量PooledHeapByteBuf，实际上并没有，因此可以确认发生了堆外内存泄漏，即请求消息
没有释放或没有被及时释放导致的内存泄露。

对请求消息的内存分配进行分析，发现在NioByteUnsafe.read申请了内存，对allocate分析，发现调用的是
DefaultMaxMessagesRecvByteBufAllocator$MaxMessageHandler.allocate。最终调用PooledByteBufAllocator.newDirectBuffer。

请求ByteBuf的创建分析完，继续分析释放，由于业务的RouterServerHandler继承自ChannelInBoundHandlerAdapter，channelRead执行完成，
ChannelHandler的执行就结束。
通过分析，请求ByteBuf被netty申请后就没有释放。
为了验证，在代码中调用ReferenceCountUtil.release进行释放内存，压测发现系统平稳。


更好地管理ByteBuf，4种场景
1.基于内存池的请求ByteBuf
包括PooledDirectByteBuf和PooledHeapByteBuf由netty的nioEventLoop线程在处理Channel的读操作时分配，需要在业务ChannelInboundHandler
处理完请求消息之后释放(通常在解码之后)，他的释放有两种策略。
a.业务handler继承自SimpleChannelInBoundHandler，实现channelRead0，ByteBuf的释放由SimpleChannelInBoundHandler负责
b.业务handler中调用ctx.fireChannelRead，让请求消息继续向后执行，直到DefaultChannelPipeline的内部类TailContext，由它负责释放
2.基于非内存池的请求ByteBuf
业务使用非内存池模式，覆盖netty默认的内存池模式创建请求ByteBuf，如重写initChannel中ch.config().setAllocator(UnpooledByteBufAllocator.DEFAULT)
也需要按照内存池的方式释放内存
3.基于内存池的响应ByteBuf
调用writeAndFlush或flush，在消息发送完成后都会由netty框架进行释放内存
4.基于非内存池的响应ByteBuf
无论是基于内存池还是非内存池分配的ByteBuf，
若是堆内存，则将堆内存转换成堆外内存，然后释放HeapByteBuf，待消息发送完成，释放转换后的DirectByteBuf。
若是DirectByteBuffer，不用转换，待消息发送完后释放。
因此，对于需要发送的响应ByteBuf，由业务创建，但不需要由业务释放。


通过内存池技术重用这些临时对象，可以降低gc频次和减少耗时，提升系统的吞吐量


netty内存池工作原理



跨线程操作ByteBuf使用注意：
1.要防止netty的NioEventLoop线程与应用线程并发操作
2.ByteBuf的申请和释放，避免忘记释放、重复释放，以及释放后继续访问
注意频繁内存拷贝问题


ByteBuf通过两个指针来协助缓冲区的读写操作，读操作用readerIndex，写操作用writerIndex
discardable bytes|readable bytes(conent)|writeable bytes |
0							 readerIndex            writerIndex         capacity
调用discardReadBytes则释放0~readerIndex空间


ByteBuf引用计数器原理
AbstractReferenceCountedByteBuf实现了对ByteBuf的内存管理，通过引用计数器对ByteBuf的引用情况管理，跟踪，以实现内存的回收、销毁和重复利用



netty发送队列积压
1个客户端访问服务器，基于netty通信，压测一段时间后，响应时间越来越长，失败率增加，监控客户端内存使用情况，内存一直飙升，最后oom，cpu占用居高不下，吞吐量0
dump客户端内存文件进行分析，发现netty的nioEventLoop占用了99%内存，
继续对引用关系分析，发现真正泄露的是WriteAndFlushTask，它包含了待发送的客户端请求消息msg及promise对象。

通过源码发现，调用channel.write方法时，若发送方为业务线程，则将发送操作封装成writeTask放入NioEventLoop中执行(AbstractChannelHandlerContext.write)
显然Netty的I/O线程NioEventLoop无法完成如此多消息的发送，因此发送任务队列积压，导致oom
利用netty提供的高低水位机制，可以实现客户端更精准的流控


导师消息积压可能场景：
1.网络瓶颈，发送超过网络链接处理能力，会导致发送队列积压
2.服务端读取速度小于发送方发送速度，导致自身TCP发送缓冲区满，频繁发生write0字节时，待发送消息会在netty发送队列中排队

利用netstat -ano等命令可以监控某个端口的TCP接收(recv-q)和发送(send-q)队列积压情况。日常监控，将netty的链路数、网络读写速度等指标纳入监控系统，告警



netty消息发送工作机制
调用write后，经过channelPipeline处理，消息被投递到发送缓冲区待发送，调用flush之后会执行真正的发送操作，底层通过调用javaNio的SocketChannel进行非阻塞write操作。


为尽可能提升性能，netty采用了串行无锁化设计，I/O线程内进行串行操作，避免多线程竞争导致性能下降。
netty串行化工作原理：
1.当用户线程发起write时，若不是nioEventLoop(I/O线程)则将发送消息封装成WriteTask放入NioEventLoop的任务队列(AbstractChannelHandlerContext.write)
2.nioEventLoop线程内部维护了一个taskQueue，除了网络I/O读写操作，还负责执行网络读写相关和用户的的task(SingleThreadEventExecutor)
3.NioEventLoop遍历taskQueue，执行发送任务
4.ChannelOutboundBuffer.addMessage

结论：
1.多个业务线程并发调用write方法线程安全，netty会将发送消息封装成task，由I/O线程异步执行
2.由于单个Channel由其对应的NioEventLoop线程执行，若并行调用某个Channel的write超过对应的NioEventLoop线程执行能力，则会导致WriteTask积压
3.NioEventLoop线程需要处理网络读写，以及注册到其上的各种task，相互影响。

ChannelOutBoundBuffer是netty的发送缓冲队列，基于链表管理

发送次数限制：
netty采用折中方式，若本次发送的字节数大于0，但消息尚未发送完，则循环发送，一旦发现write字节数为0，说明tcp缓冲区已满，此时继续发送没有意义，注册
SelectionKey.OP_WRITE退出循环，在下一个SelectionKey轮询周期继续发送(NioSocketChannel.doWrite)

若消息发送成功，则netty会释放已发送消息的内存

发送消息高低水位控制
ChannelOutBoundBuffer.incrementPendingOutboundBytes，业务可监听该事件fireChannelWritabilityChanged



高比昂发压测性能波动问题
性能压测4000qps一段后，cpu占用飙升，性能极具下降，最低到0，停止压测一段时间，系统恢复，再压测一段时间，用户并发量大之后，性能又急剧下降，形成周期性波动，吞吐量不稳定
采集线程堆栈，发现GC线程占用大量CPU资源
由于停压后，还能恢复，所以不存在内存忘记释放导致的泄露问题。
dump内存堆栈，通过map进行top内存占用分析，Giggest Objects，jdk的线程池占用内存大。切换到类实例图，Histogram中按Retained Heap排序，char占用多。
通过Dominator Tree查看引用，发现char数组被jdk线程池的LinkedBlockingQueue引用

结合代码分析，api网关每次接收请求消息，无论消息大小，都构造64KB的char数组，用于处理和转发请求。若后端处理消息慢，则导致任务队列积压，由于每个任务(Runable)持有一个
64KB的char数组，所以积压多了转移到老年代，触发gc，吞吐量为0

由于当前网关平台转发的请求报文都较小(1kb左右)，进行优化，按照请求消息大小初始化char数组

原理：当内存申请和释放速度能够相对平衡时，系统运行就平稳，反之若老年代经常发生堆积，频繁gc则系统很难稳定


主动oom法则，将最大堆内存尽量调小，让系统尽快oom，多采集几次oom的内存堆栈信息对比，以及oom日志，结合dump堆栈进行分析


网关类产品优化建议：
主要功能就是消息的预处理和转发，请求和响应对象都是招生熄灭，高并发场景下，一定要放置不合理的内存申请，措施：
1.内存按需分配。
2.不要频繁地创建和释放对象。可以采用内存池等机制优化内存的申请和释放
3.减少对象拷贝。对于透传类，尽量把涉及业务逻辑处理的字段放入header，不要对body解码，直接透传到后端服务。这样减少内存的申请和对象拷贝，降低内存占用，提升性能。
4.流量控制机制。除客户端并发连接数流控，qps流控，还需针对内存占用等指标做流控，防止业务高峰期的oom


netty消息接入的内存申请方式及ByteBuf容量不足时动态扩容机制：
NioByteUnsafe.read，默认通过AdaptiveRecvByteBufAllocator进行内存分配。


ChannelHandler一端是nettyNio线程，另一端则是业务线程池。
对于channelHandler中用到的业务Handler，是否线程安全。
1.若channelHandler是非共享，则它就是线程安全。
因为，链路完成初始化时会创建ChannelPipeline，每个Channel对应一个ChannelPipeline实例，业务类会被实例化并放入Channelpipeline执行。
由于某个Channel只能被特定的NioEventLoop线程执行，所以业务Handler安全。
2.若业务代码用多线程调用ChannelHandlerContext或Channel的write方法，会不会导致多个业务线程并发调用业务Handler呢？
不会，因为write时，判断是否是下一个要执行write的AbstractChannelhandlerContext的EventExecutor线程，若不是则将write封装成AbstractWriteTask放入线队列，
源线程返回，若业务ChannelHandler没有指定EventExecutor线程，则用消息读写对应的NioEventLoop线程(AbstractChannelHandlerContext.write)
3.若只初始化一次业务handler，加到多个channel的channelPipeline中。
因为不同的channel可能绑定不同的nioEventLoop线程，这样业务handler可能被多个I/O线程访问，存在并发问题。
运行报错，handler is not a @Sharable handler,so can't be added or removed multiple times.把同一个业务handler放入多个ChannelPipeline时发生异常。
注：有很多EventLoop可以使用，而每个channel会被绑定到某一个eventLoop上。



netty的ChannelHandler工作机制
将Channel的数据管道抽象为ChannelPipeline，消息在ChannelPipeline中流动和传递。
ChannelPipeline持有ChannelHandler的链表，由ChannelHandler对I/O事件进行拦截和处理，可实现对修改封闭对扩展开放。
ChannelPipeline是ChannelHandler的编排管理容器，内部维护了一个ChannelHandler的链表和迭代器，方便实现ChannelHandler的查找、添加、替换和删除
数据流向：
socket.read->ChannelHandler1-->ChannelHandlerN
socket.write->ChannelHandlerN-->ChannelHandler1

工作原理：
1.底层SocketChannel.read读取ByteBuf，触发ChannelRead事件，由I/O线程NioEventLoop调用ChannelPipeline的fireChannelRead，将ByteBuf传到ChannelPipeline
2.消息依次被HeadContext、ChannelHandler1、TailHandler拦截和处理，任何ChannelHandler都可中断当前流程，结束消息的传递
3.调用ChannelHandlerContext的write发送消息，消息从TailContext开始，途径ChannelHandlerN、ChannelHandler1、HeadHandler，最终被添加到消息发送缓冲区等待
刷新和发送，此过程中也可以中断消息的传递，构建异常Future

netty中事件分为inbound和outbound
inbound通常由I/O线程触发，如TCP链路建立、链路关闭、读、异常通知，用户通过实现ChannelInboundHandler完成消息的读取、反序列化、鉴权等。
outbound事件由用户主动发起的网络I/O操作，如发起连接、绑定、消息发送，通过实现ChannelOutboundHandler拦截并处理。
若需同时处理inbound和outbound则实现ChannelDuplexHandler。

ChannelPipeline线程安全，可以被多个NioEventLoop线程并发调用，也可在运行过程中动态地删除和添加ChannelHandler。
ChannelPipeline通过链表方式管理ChannelHandler，每个ChannelHandler对应一个上下文ChannelHandlerContext，新加入ctx会更新指针
对于耗时业务逻辑执行，需要切换到业务线程池中，避免阻塞netty的NioEventLoop线程，导致无法接收和发送。

通过ChannelHandlerContext.firUserEventTriggered或者Channel.pipeline.fireUserEventTriggered可以实现业务自定义事件的发送


车联网服务端接收不到车载中断终端消息案例
当服务端出现性能瓶颈或者阻塞时，会导致终端设备连接超时和掉线，引发各种问题，在物联网场景下，一定要防止服务端因编码不当导致的意外阻塞，进而无法处理终端请求消息
问题：
车联网服务端用netty构建，接收车载终端的请求消息，然后下发给后端其他系统，最后返回应答给车载终端。系统运行一段时间后发现服务端接收不到车载终端消息，导致业务中断。
日志看，服务端每隔一段时间，就收不到消息，但是隔一段时间后又恢复，周而复始。
与终端确认，终端设备每隔固定时间会发送消息给服务端，因此排除终端问题。
采集服务端cpu使用情况，发现并不是瓶颈。
是不是内存问题，导致GC引起业务线程暂停？采集GC统计数据。同样排除问题
现象看，服务端接收不到消息，排除gc、网络等问题后，很可能是netty的NioEventLoop线程阻塞，导致TCP缓冲区的数据没有及时读取，故障期间的线程堆栈。
从堆栈看，netty的nioEventLoop读取消息后，调用业务线程池执行业务逻辑时触发RejectedExecutionException异常，由于后续业务逻辑由NioEventLoop线程执行，
因此可以判断业务使用了CallerRunsPolicy策略，即在业务线程池消息队列满后，由调用方的线程来执行当前Runnable。NioEventLoop在执行业务任务时发生了阻塞，
导致NioEventLoop线程无法处理网络读写消息，因此会看到服务端没有消息接入，但是从阻塞状态恢复后，就可以继续接受消息。



由于channelHandler是业务代码和netty框架交互的地方，所以业务里面的逻辑通常由nioEventLoop线程执行，因此放置业务代码阻塞nioEventLoop线程显得很重要
常见阻塞情况：
1.直接在ChannelHandler中写可能导致阻塞的代码，如数据库操作、第三方服务调用、中间件服务调用、同步获取锁、sleep等
2.切换到业务或业务消息队列做异步处理时发生了阻塞，典型的有阻塞队列、同步获取锁等


推荐业务处理线程和netty网络I/O线程分离，原因：
1.充分利用多核并行处理能力：I/O线程和业务线程分离，双方可以并行处理。
2.故障隔离：后端的业务线程池处理各种类型的任务，如I/O密集型、CPU密集型、纯内存计算型，不同时延，以及故障概率。


NioEventLoop负责：
1.纯粹I/O操作，负责I/O读写操作
2.系统任务：调用NioEventLoop.execute，netty有很多系统任务，主要原因是，当I/O线程和用户线程同时操作网络资源时，为了防止并发操作导致的锁竞争，将用户线程
的操作封装成task放入队列，由I/O线程负责执行，实现了局部无锁化。
3.定时任务：调用NioEventLoop.schedule系列方法


I/O读写操作原理：
Reactor服务端线程模型-示意图：
NioEventLoopGroup(boss Thread)				      NioEventLoopGroup(work Thread)
client-->Reactor Thread(Acceptor)    --Dispatcher-->   Reactor Thead Pool
|					    |
Handler...    Handler...

NioEventLoop作为Reactor线程，负责TCP连接的创建和接入，以及TCP消息的读写
Reactor线程职责：
1.作为NIO服务端，接收客户端的TCP连接
2.作为NIO客户端，向服务端发起TCP连接
3.读取通信对端的请求或应答消息
4.向通信对端发送消息请求或应答请求
Reactor模式使用的是异步非阻塞I/O，理论上一个线程可以独立处理所有I/O相关操作，不过对于高负载、大并发的应用场景不合适理由：
1.一个NIO线程同时处理成百上千条链路，在性能上无法支撑，即便NIO线程的CPU负载100%，也无法满足海量消息的编解码、读取和发送
2.当NIO线程负载过重，处理速度变慢，会导致大量客户端连接超时，之后又重发，加重NIO线程的负载，最终导致大量消息积压和处理超时，系统瓶颈。
3.可靠性问题：一旦NIO线程意外跑飞，或者进入死循环，会导致整个系统通信模块不可用，不能接收和处理外部消息，造成节点故障。
针对以上，可以在创建NioEventLoopGroup时指定工作的I/O线程数，可提升网络的读写性能。

NioEventLoop线程处理网络读写操作关键是聚合了一个Selector(NioEventLoop中)。
netty对Selector的遍历也做了性能优化，对于网络消息的处理，通过轮询Selector的SelectedSelectionKeySet实现(processSlectedKey)。
通过对SelectionKey的取值进行判断，完成对应的I/O操作(有感兴趣的事件已经准备好)：
1.若为OP_CONNECT,则代表客户端异步连接操作执行成功。
2.若为OP_WRITE,说明发生了写半包，发送队列尚有消息未完成发送，需要继续执行发送操作
3.若为OP_READ，说明SocketChannel上有消息可以读取，需要执行read ByteBuffer操作
4.若为OP_ACCEPT，说明ServerSocketChannel上有新的客户端TCP连接接入，需要执行accept操作，完成TCP握手和客户端TCP连接的接入


NioEventLoop也支持各种Runnable类型的任务的执行，任务的使用场景：
1.netty系统任务，主要用于任务的异步执行，或者用于从用户线程切换到netty的nioEventLoop线程，避免业务handler加锁。
2.用户自定义用来辅助I/O操作的业务任务


AbstractWriteTask是典型的netty系统任务，将write操作封装成任务放入nioEventLoop任务队列异步执行。
每次Selector轮训完，执行taskQueue中的任务
由于NioEventLoop需要同时处理I/O事件和非I/O任务，为了保证两者都能得到足够的cpu时间，netty提供I/O比例供用户定制。当限制的执行时间到期时，无论
当前积压的任务是否执行完，都要退出循环，防止长时间执行任务而阻塞网络I/O操作。


NioeventLoop还支持执行定时任务，调用schedule。
通过调用fetchFromScheduledTaskQueue，将到期的定时任务加入taskQueue并随taskQueue执行，也可以理解为taskQueue本身就是需要立即执行的定时任务。
netty中，定时任务经典使用场景是链路空闲状态监测，在初始化IdleStateHandler时，同步创建ReaderIdleTimeoutTask、WriterIdleTimeoutTask和AllIdleTimeoutTask
三个定时任务，负责链路空闲状态检测(initialize)。
对于用户，若需要执行一些周期性任务，可以直接使用netty的NioEventLoop定时任务，实现诸如心跳发送等功能


netty多线程编程最佳实践：
1.创建两个NioEventLoopGroup，用于逻辑隔离NIO Acceptor和NIO I/O线程
2.尽量不要在业务ChannelHandler中启动用户线程(解码后用于将POJO消息派发到后端业务线程除外)
3.解码要放在NIO线程调用的解码Handler中，不要切换到用户线程完成消息的解码
4.若业务逻辑简单(纯内存操作)，没有复杂的业务逻辑计算，也没有可能导致线程被阻塞的磁盘操作、数据库操作、网络操作等，可以直接在NIO县城上完成，不用切换用户线程
5.若业务逻辑复杂，不要在NIO线程上完成，建议将解码后的POJO消息封装成任务，派发到业务线程池中由业务线程执行，以保证NIO线程尽快被释放，处理其他I/O操作。


推荐的线程数量计算公式：
公式1：线程数=(线程总时间/瓶颈资源时间)*瓶颈资源的线程并行数
公式2：QPS=1000/线程总时间*线程数
实际上需要根据测试数据和用户场景，结合公式给出一个相对合理的范围，然后进行性能测试选择相对最优值。

编写代码时，始终考虑NioEventLoop线程是否会被业务代码阻塞，只有消除所有可能导致的阻塞点，才能保证程序稳定运行。




netty4中，当调用write时，netty会将发送事件封装成任务，放入nioEventLoop的任务队列异步执行，然后业务调用线程返回并继续执行，若在相应对象编码之前，业务线程
和netty的NioEventLoop线程同时修改了返回对象，就会发生并发问题。



升级netty3->4后上下文丢失问题
为提升业务的二次定制能力，降低对接口的侵入性，业务使用线程变量进行消息上下文的传递。业务也使用了一些第三方开源容器，也提供了线程级变量上下文的功能。
升级后，业务的ChannelHandler发生了空指针异常，无论是业务自定义的线程上下文，还是第三方容器的线程上下文，都取不到
调试发现，业务ChannelHandler获取的线程上下文对象和之前业务传递的上下文对象不是同一个，说明执行ChannelHandler的线程跟处理业务的线程不是同一个。
netty4修改了outbound的线程模型。
所以，必须对它的线程模型有深入和清晰的了解。



升级netty3->4后性能下降
通过对热点方法的分析，发现消息发送过程中，两处：
1.消息发送性能统计相关ChannelHandler
2.返回响应时的编码ChannelHandler

对比netty3和netty4的业务产品，发现上述两个都是热点，为什么切换到4性能下降？
通过方法的调用堆栈分析发现，两个版本差异：netty3中，上述两个热点方法由业务线程负责执行；netty4由NioEventLoop(I/O)线程执行。
对于某个链路的write操作，业务侧拥有多个线程的线程池，而NioEventLoop线程只有一个，所以执行效率更低，返回客户端的应答时延就大。
随着时延增大，导致系统并发量降低，性能降低。

针对netty4的线程模型对业务进行专项优化，在业务线程中将耗时的反序列化等逻辑操作完成，不在业务ChannelHandler中执行，性能达到预期。

性能优化建议：
适当地调大worker线程组的线程数(NioEventLoopGroup)，分担每个NioEventLoop线程的负载，提升ChannelHandler执行的并发读。
将业务上耗时的操作从ChannelHandler中移除，放入业务线程池处理，提升对热点代码的执行效率。



netty3.x的I/O操作分为两大类事件
1.Upstream ChannelEvent：主要包括链路建立事件、链路激活事件、读事件、I/O异常事件、链路关闭事件等
2.Downstream ChannelEvent：主要包括写事件、连接事件、监听绑定事件等

netty3的Upstream ChannelEvent相关线程模型：
worker线程(I/O)--socket.read-->ChannelEvent--Upstream-->ChannelPipeline(Handler1..EndHandler)
|
业务逻辑线程1..N<--业务线程池<----线程切换：放入消息队列，I/O线程返回	<--Netty I/O线程执行

以上，Upstream事件的主要处理流程：
1.I/O线程(worker线程)将消息从TCP缓冲区读取到SocketChannel的接收缓冲区
2.由I/O线程负责生成相应的事件(如UpstreamMessageEvent)，触发事件向上执行，调度到ChannelPipeline中
3.I/O线程调度执行ChannelPipeline中Handler链的方法，直到业务实现的最后一个Handler
4.最后一个Handler将消息封装成Runnable，放入业务线程池执行，I/O线程返回，继续执行读、写等I/O操作
5.业务线程池从任务队列中获取消息，并发执行业务逻辑

可以看出Upstream操作相关的Handler都是由Netty的I/O worker线程负责执行


netty3的Downstream ChannelEvent相关线程模型：
业务线程--Channel.write-->ChannelEvent--DownStream-->ChannelPiepline(Handler1..EndHandler)
|
弹出待发送消息，调用SocketChannel.write<--I/O线程从队列取执行<--线程切换：放入消息队列，业务线程返回<--

以上，Downstream ChannelEvent的主要流程：
1.业务线程发起Channel写操作
2.netty将写操作封装成Dwonstream ChannelEvent，触发事件向下传播
3.写事件被调度到ChannelPipeline中，由业务线程按照Handler链串行调用支持Downstream事件的ChannelHandler
4.执行到最后一个Handler，将编码后的消息提供给发送队列，业务线程返回
5.netty的I/O线程从发送消息队列中取出消息，调用SocketChannel.write



netty4线程模型
所有的I/O操作，无论是netty的I/O线程发起，还是业务发起的，都统一由netty的NioEventLoop线程执行

Netty4的inbound和outbound操作线程模型-示意图
SocketChannel.read                     																			  ChannelPipeline(Handler1)					<---Channel.write
NioEventLoop(ChannelOutBoundBuffer)											..↑outbound				   业务线程
flush待发送消息，调用SocketChannel.write																													EncodeHandler
↓inbound
TailHandler
I/O线程执行		 			 		
读写流程：
1.I/O线程NioEventLoop从SocketChannel中读取数据报，将ByteBuf放入ChannelPipeline，触发ChannelRead事件
2.I/O线程NioEventLoop调用ChannelHandler职责连，直到消息投递到业务线程，然后I/O线程返回，继续后续的读写操作
3.业务线程调用ChannelHandlerContext.write进行消息发送
4.若由业务线程发起的写操作，ChannelHandlerInvoker将发送消息封装成任务，放入I/O线程NioEventLoop的任务队列，由NioEventLoop在Selector轮询中统一调度
和执行。放入任务队列后，业务线程返回
5.I/O线程NioEventLoop调用ChannelHandler职责连，进行消息发送，处理OutBound事件，直到将消息放入发送队列，然后唤醒Selector执行写操作。


netty4在调用ChannelHandlerContext或者Channel的write时，会对当前执行线程判断，由下一个将要执行的ChannelHandler绑定的EventExecutor线程执行，
默认是当前Channel注册的NioEventLoop线程(与read操作时同一个线程)，将发送消息封装成WriteTask，加入NioEventLoop线程的任务队列异步执行，
当前调用方业务线程返回。

netty4采用串行化设计理念，从消息的读取、编码到后续的Handler执行，始终由I/O线程NioEventLoop负责。
一个NioEventLoop聚合了一个Selector，因此可以处理成百上千的客户端连接。netty的处理策略是，每当有一个新的客户端接入，从NioEventLoop线程组中顺序获取
一个可用的NioEventLoop。
一个客户端连接只注册到一个NioEventLoop，避免了多个I/O线程并发操作它。

对netty的版本升级需要从功能、兼容性和性能等多个角度综合考虑。

流程：
注册：
当客户端来连接时，服务端selector感知到注册的连接事件，获取到这此的channel，然后从NioEventLoopGroup中获取一个NioEventLoop绑定这个channel并操作其
内容。NioEventLoop同样会注册到selector上感兴趣的事件，当有读数据来时，selector感知到，然后通知NioEventLoop进行这个channel上的操作。
然后就会调用到pipeline进而调用到业务的handler，这样数据就来了。当write时也是基于这个NioEventLoop进行操作，通过handler链最后到达selector那里写出去。




业务ChannelHandler无法并发执行问题
业务通过DefaultEventExecutorGroup并行执行LogicServerHandler，性能压测时发现服务端的处理能力非常差，感觉多线程没生效。

吞吐量是个位数，考虑到业务处理逻辑好事在100ms~1000ms，怀疑ChannelHandler并没有被并发执行，而是被单线程执行
验证猜想，查看服务端的线程堆栈，发现线程组中只有一个DefaultEventExecutor线程在运行
对原发分析，看addLast，创建DefaultChannelHandlerContext，调用childExecutor，从EventExecutorGroup中选择一个EventExecutor绑定到DefaultChannelHandlerContext
发现对于某个具体的TCP连接，绑定到业务ChannelHandler实例上的线程池为DefaultEventExecutor。
分析接收请求时，业务ChannelHandler的调用情况，AbstractChannelHandlerContext.invokeChannelRead，业务ChannelHandler绑定的EventExecutor为
DefaultEventExecutor，继承自SingleThreadEventExecutor，执行executor就是把Runnable放入任务队列由单线程执行。

针对如上问题，按照业务场景，两种优化策略：
1.按照真实的业务需求，若所有客户端的并发链接数(TCP/HTTP链路数)小于业务需要配置的线程数，建议将请求消息封装成任务，放入后端业务线程池执行，ChannelHandler不
需要处理复杂业务逻辑，也不需要再绑定EventExecutorGroup
2.若所有客户端的并发链接数大于或等于业务需要配置的线程数，客户端提高并发，服务端还为业务ChannelHandler绑定EventExecutorGroup，并在业务ChannelHandler中执行各种业务逻辑。



DefautEventExecutor工作机制
是SingleThreadEventExecutor的一个实现，对应的还有NioEventLoop。
是一个典型的Event Reactive Thread实现，各种任务被加入任务队列，由一个工作线程循环执行。
与NioEventLoop主要差别是：NioEventLoop侧重于处理网络I/O相关的各种事件，另一个侧重于处理业务相关的逻辑处理。

由于继承了ScheduledExecutorService和ExecutorService接口，所以与NioEventLoop线程类似，可以同时处理普通的Runnable和定时任务。
对于NioEventLoop除了处理任务队列、定时任务，还需要同时轮询Selector，处理网络I/O操作，并对双方的执行时间进行控制，防止一方执行过长


优化方案
当netty服务端接入的客户端连接数较多时，使用EventExecutorGroup绑定业务ChannelHandler或者在后台创建一个jdk线程池专门处理业务逻辑操作对比：
1.对于某个客户端连接Channel，只会注册到一个NioEventLoop线程中，用于处理网络I/O操作，业务的ChannelHandler指定了运行线程池EventExecutorGroup之后，
创建ChannelHandlerContext上下文时会从EventExecutorGroup选择一个EventExecutor绑定到该Channel对应的ChannelHandler实例。
这样，对于某个Channel，两侧都是进行了线程绑定，消息接收线城是NioEventLoop，业务逻辑处理在EventExecutor线程中，实现了网络I/O线程于业务逻辑处理线程的绑定，
对于某个TCP连接，由于双方是一对一关系，降低了锁竞争，当客户端并发链接比较多时，会有N个Channel并行处理。
2.后端创建一个统一的jdk线程池做业务逻辑处理方案，网络I/O处理线程NioEventLoop(多个)与业务线程池会形成M对N的映射，由于jdk的ThreadPoolExecutor采用一个阻塞
队列+n个工作线程模型，若业务线程比较多，会形成激烈的锁竞争。
优化方向是尽量消除锁竞争，思路：
a.利用netty的ChannelId绑定业务线程池的某个业务线程，后续该Channel的所有消息读取和发送都由板顶的NioEventLoop和业务线程执行，把锁竞争降低到最低
b.业务线程池采用一个线程对一个一个消息队列的方式，降低队列的锁竞争。



Netty线程绑定机制原理：
NioEventLoop线程与SocketChannel绑定关系，channelRead中，
当服务端接收客户端TCP连接时，触发channelRead方法，由ServerBootstrapAcceptor负责将新接入的SocketChannel注册到NioEventLoop的Selector，同时建立
SocketChannel和NioEventLoop的绑定关系，MultithreadEventLoopGroup.register
业务ChannelHandler异步执行的线程切换点发生在AbstractChannelHandlerContext.invokeChannelRead中,
由NioEventLoop线程切换到业务ChannelHandler绑定的DefaultEventExecutor中

把业务逻辑单独拆分到业务线程池中进行处理，与I/O线程隔离时，不同的业务线程模型对性能的影响也非常大。



当客户端的并发链接数达到数十万或数百万时，系统一个小的抖动就会导致严重的后果，例如服务端的GC，导致应用暂停(STW)的GC持续几秒，会导致海量的客户端设备掉线或
消息积压，一旦系统恢复，会有海量的设备接入或海量的数据发送，可能瞬间把服务端冲垮

希望：单节点性能足够高。

要想实现海量设备的接入，需要对操作系统参数、netty框架、jvmgc参数，甚至业务代码做针对性的优化，各种优化要素互相影响，设置或组合不敢当就容易导致性能问题。



海量设备内存泄漏问题
服务端MQTT消息服务中间件，保持10万用户在线长连接，2万用户并发消息请求。运行一段时间后，发现内存泄漏，甚至怀疑是netty的bug。
相关资源：
1.硬件资源：MQTT消息服务器内存16GB，CPU8核
2.netty中boss线程池1，worker线程池8，其余线程分配给业务使用。后来调整worker为16，问题依旧

内存泄漏原因定位：
dump内存堆栈，对疑似内存泄漏的对象和引用关系分析
发现netty的ScheduledFutureTask增加9076%，达到110万个实例，对业务代码分析，用户用IdleStateHandler在链路空闲时进行业务逻辑处理，但空闲时间设置得比较长，15分钟。
netty的IdleStateHandler会根据用户的使用场景，启动三类定时任务：ReaderIdleTimeoutTask、WriterIdleTimeoutTask、AllIdleTimeoutTask，
都会加入NioEventLoop的任务队列调度和执行。
由于超时时间过长，10万个长连接会创建10万个ScheduledFutureTask对象，每个对象还保存了业务的成员变量，非常消耗内存。
用户的老年代设置比较大，一些定时任务被晋升到老年代，没有被新生代GC回收，导致内存一直增长，用户误认为存在内存泄露。

通过进一步分析发现，用户的超时时间设置得非常不合理，15分钟达不到快速检测设备是否掉线的设计目的，将超时时间设置为45秒后，内存可以正常回收，问题解决。
--应该是说可以在45s内发送然后回收内存，而里面的ScheduledFutureTask还是会存在的，因为总得到时间发送，可能是说量大导致直接晋升到老年代，误以为泄露。

思考：
若是100个长连接，即便是长周期的定时任务，也不存在内存泄露问题，在新生代通过minorGC就可以实现内存回收。正式因为10万数量级长连接，导致小问题被放大，引发后续各种问题。
若用户确实有长周期的定时任务，该如何处理？


操作系统参数调优：
实现百万级的长连接接入
1.文件描述符
a.查看系统最大文件句柄数：cat /proc/sys/fs/file-max
配置完成执行sysctl -p让配置生效

b.单进程打开的最大句柄数
ulimit -a 中的open files数量
当并发接入的TCP连接数超过上限时，提示too many open files，通过/etc/security/limits.conf添加配置：
soft nofile 1000000
hard nofile 1000000
重新登录，再通过ulimit -a 查看

2.tcp相关参数
a.net.ipv4.tcp_rmem:为每个TCP连接分配的读缓冲区内存大小
第一个值是socket接收缓冲区分配的最小字节数。第二个值时默认值，缓冲区在系统负载不高时可以增长到该值。第三个值时接收缓冲区分配的最大字节数
b.net.ipv4.tcp_wmem:为每个TCP连接分配的写缓冲区内存大小。
三个值：socket发送缓冲区分配的最小字节数。默认值，缓冲区在系统负载不高的情况下可以增长到该值。发送缓冲区分配的最大字节数
c.net.ipv4.tcp_mem:内核分配给TCP连接的内存，单位是page(1个page通常是4096字节，可通过getconf PAGESIZE查看)，包括最小、默认和最大三个配置项
d.net.ipv4.tcp_keepalive_time:最近一次数据包发送与第一次keep alive探测消息发送的时间间隔，用于确认TCP连接是否有效
e.tcp_keepalive_intvl:在未获得探测消息响应时，发送探测消息的时间间隔
f.tcp_keepalive_probes:判断TCP连接失效连续发送的探测消息个数，达到之后判定连接失效
g.net.ipv4.tcp_tw.reuse:是否允许将TIME_WAIT Socket重新用于新的TCP连接，默认0，表示关闭
h.net.ipv4.tcp_tw_recycle:是否开启TCP连接中TIME_WAIT Socket的快速回收，默认0表示关闭
i.net.ipv4.tcp_fin_timeout:套接字自身关闭时保持在FIN_WAIT_2状态的时间，默认60  --发送fin并收到对端ack?

通过/etc/sysctl.conf对上述修改(大约可以接入50万个连接)
net.ipv4.tcp_mem = 65608 1048576 2097152
net.ipv4.tcp_wmem = 4096 87380 4194304
net.ipv4.tcp_rmem = 4096 87380 4194304
net.ipv4.tcp_keepalive_time = 1800
net.ipv4.tcp_keepalive_intvl = 20
net.ipv4.tcp_keepalive_probes = 5
net.ipv4.tcp_tw_reuse = 1
net.ipv4.tcp_tw.recycle = 1
net.ipv4.tcp_fin.timeout = 30
执行sysctl -p使配置生效


多网卡队列和软中断
随着网络带宽不断提升，单核CPU不能完全满足网卡的需求，通过多队列网卡驱动的支持，将各个队列通过中断绑定到不同的CPU内核，以满足对网络吞吐量要求比较高的业务场景
多队列网卡需要网卡硬件支持，
判断当前系统是否支持多队列网卡，
lspci -vvv
或 ethtool -l 网卡 interface 名
查看网卡驱动型号，根据网卡驱动官方说明确认

对于不支持多队列网卡的系统，若内核版本支持RPS(kernel 2.6.35及以上)，开启RPS后可以实现软中断，提升网络的吞吐量。
RPS根据数据包的源地址、目的地址及目的和原端口，算出一个hash值，根据这个hash值选择软中断运行的CPU，从上层来看，也就是将每个连接和CPU绑定，并通过这个hash值，
在多个CPU上均衡软中断，提升网络并行处理性能，它实际提供了一种通过软件模拟多队列网卡的功能

3. 设置合理的线程数
   集中在用于接收海量设备TCP连接、TLS握手的Acceptor线程池(boss NioEventLoopGroup)上，以及用于处理网络读写、心跳发送的I/O工作线程池(work NioEventLoopGroup)上

对于netty服务端，通常只启动一个监听端口用于端侧设备的接入，但若服务端集群实例比较少，当端侧设备在短时间内大量接入时，需要对服务端的监听方式和线程模型做优化，以满足
短时间内(如30s)百万级的端侧设备接入的需要
服务端可以监听多个端口，利用主从Reactor线程模型做接入优化，前段通过SLB做4层/7层的负载均衡。
主从Reactor线程模型特点：
服务端用于接收客户端连接的是一个独立的NIO线程池；Aceeptor接收到客户端TCP连接请求并处理后(可能包括接入认证等)，将新创建的SocketChannel注册到I/O线程池
(sub Reactor线程池)的某个I/O线程，由它负责SocketChannel的读写和编解码；
Acceptor线程池仅用于客户端的登录、握手和安全认证等。
一旦链路建立成功，将链路注册到后端sub Reactor线程池的I/O线程，由其负责后续的I/O操作。
由于同时监听了多个端口，每个ServerSocketChannel都对应一个独立的Acceptor线程，这样就并行处理，加速端侧设备的接入，减小端侧设备的连接超时失败率，提升服务端单节点的处理性能

对I/O工作线程池优化
先采用默认值，进行性能压测，过程中采集I/O线程的CPU占用，看是否存在瓶颈，策略：
a.ps -ef | grep java
b.top -Hp pid 查看该进程下所有线程的运行，shift +p 对CPU占用大小排序，获取线程的pid及对应的cpu占用大小
c.printf '%x\n' pid，将pid转成16进制
d.jstack -f pid获取线程堆栈， 找到I/O work工作线程，
若连续采集几次对比，发现线程堆栈都停留在SelectorImpl.lockAndDoSelect处，说明I/O线程比较空闲。
若发现I/O线程的热点停留在读、写或在ChannelHandler的执行处，可以通过适当调大NioEventLoop线程的个数提升网络的读写性能。
1)接口API指定：在创建NioEventLoopGroup实例时指定线程数
2)系统参数执行：-Dio.netty.eventLoopThreads指定。弊端：是系统配置，一旦设置，所有创建NioEventLoopGroup未指定线程数的地方都使用此配置。


4. 心跳优化(针对海量设备接入的服务端)
   a.及时检测失效的连接，将其剔除，防止无效的连接句柄积压，导致OOM
   b.设置合理的心跳周期，防止心跳定时任务积压，造成频繁的老年代GC，导致应用暂停
   c.使用netty的链路空闲检测机制，不要自己创建定时任务线程池，加重系统的负担，以及增加潜在的并发安全

当设备突然掉电、连接被防火墙挡住、长时间GC或通信线程发生非预期异常，会导致链路不可用且不易被及时发现。特别是若异常发生在凌晨业务低谷期间，当早晨业务高峰
来时，由于链路不可用导致瞬间大批量业务失败或超时，这对系统可靠性产生大量威胁。

要解决链路的可靠性问题，必须周期性地对链路进行有效性检测。心跳检测机制分三个层面：
a.TCP层的心跳检测，即TCP的Keep-Alive机制，作用域是整个TCP协议栈
b.协议层的心跳检测，主要存在与长连接协议中，如MQTT
c.应用层的心跳检测，主要由各业务产品通过约定定时给对方发送消息

心跳检测的目的是确认当前链路是否可用，对方是否活着并且能正常接收和发送消息。
两类：
a.Ping-Pong型心跳，属于请求-响应型
b.Ping-Ping型心跳，不区分请求和应答，由通信双方约定定时向对方发送心跳Ping消息，属于双向心跳

心跳检测策略：
a.连续N次心跳检测都没有收到对方的Pong应答消息或Ping请求消息，认为链路已经发生逻辑失效，心跳超时
b.在读取和发送心跳消息时若直接发生了I/O异常，说明链路失效，心跳失败。
以上出现，都需要关闭链路，由客户端发起重连操作，保证链路能恢复正常

netty提供三种链路空闲检测机制：
a.读空闲，链路持续时间T没有读取到任何消息
b.写空闲，链路持续时间T没有发送任何消息
c.读写空闲，链路持续时间T没有接收或发送任何消息

链路空闲事件发生后触发IdleStateEvent事件，用户订阅，用于自定义逻辑处理，如关闭链路、客户端发起重新连接、告警和日志等

双向心跳检测优点:
a.及时识别网络单通、对方突然掉电等特殊异常
b.可以识别对方是否能正常工作，而不仅是网络层面的互通性检测

对于IoT场景不建议过长的心跳检测周期和超时机制：
a.百万级的长连接就有百万级的定时器，占用大量内存，若长时间存活，挥别晋升到老年代，加重CMS等老年代垃圾收集器的负担，容易导致STW
b.过长的心跳检测超时不能及时发现掉线的设备，导致大量无效的TCP连接在内存中，同时占用操作系统句柄，影响性能，也容易OOM


5. 接收和发送缓冲区调优
   一些场景，端侧设备会周期性地上报数据和发送心跳，单个链路的消息收发量不大，可以通过调小TCP的接收和发送缓冲区降低单个TCP连接的资源占用。
   .childOption(ChannelOption.SO_RCVBUF, 8*1024).childOption(ChannelOption.SO_SNDBUF, 8*1024)

6. 合理使用内存池
   物联网场景下，需要为每个接入的端侧设备至少分配一个接收和发送缓冲区对象，若采用传统的非池模式，每次消息读写都要创建和释放ByteBuf对象，若有100万个连接，每秒上报
   一次数据报或心跳，就会有100万次/秒的ByteBuf对象申请和释放，即便服务端的内存可以满足要求，GC压力也大。
   有效解决方案是用内存池，每个NioEventLoop线程处理N个连略，在线程内部，链路的处理时串行的。
   假如A链路先被处理，会创建接收缓冲区等对象，待解码完成，构造的POJO对象呗封装成任务后投递到后台的线程池中执行，然后接收缓冲区会被释放，每条消息的接收和处理都会
   重复接收缓冲区的创建和释放。若用内存池，当A链路接收到新的数据报时，从NioEventLoop的内存池中申请空闲的ByteBuf，解码后调用release将ByteBuf释放到内存池中，供后续用

netty内存池两类：对外直接内存和堆内存。由于ByteBuf主要用于网络I/O读写，因此采用堆外直接内存会减少一次从用户堆内存到内核态的字节数组拷贝，所以性能高。
由于DirectByteBuf创建成本高，因此若使用DirectByteBuf则需要配合内存池使用，否则性价比不如HeapByteBuf
netty默认的I/O读写采用内存池的堆外字节内存模式。

api设置堆内存：ch.config().setAllocator(UnpooledByteBufAllocator.DEFAULT)
参数设置堆内存：-Dio.netty.noUnsafe=true


7. 放置I/O线程被意外阻塞
   可能记录日志时，若当前自盘的WIO比较高，写日志文件操作会被同步阻塞。如log4j(1.2.x版本)，尽管支持异步写日志(AsyncAppender)，但当日志队列满时，它会
   同步阻塞业务线程(采用等待而非丢弃方式时)，直到日志队列有控线位置可用才可以。
   这就需要提前做好监控，或者调出当时出问题时间点的所有性能指标，cpu、内存、i/o、网络进行排查

8. I/O线程和业务线程分离
   若服务端不做复杂的业务逻辑操作，仅简单的内存操作和消息转发，可以通过调大NioEventLoop工作线程池的方式，直接在I/O线程中执行业务ChannelHandler，减少了一次上下文切换
   若有复杂的业务逻辑处理，建议将I/O线程和业务线程分离。
   对I/O线程，由于互相不存在锁竞争，可以创建一个大的NioEVentLoopGroup线程组，所有Channel共享同一个线程池。
   对于后端的业务线程池，建议创建多个小的业务线程池，线程池可以与I/O线程绑定。这样既减少了锁竞争，又提升了后端的处理性能。
   --可能得让I/O线程知道要绑定到哪个业务线程池？哦不对，请求进来时netty就用channel绑定了一个nioEventLoop然后执行pipelin然后执行到业务ChannelHandler，然后这就可以绑定了


9. 针对客户端并发连接数的流控
   考虑流控功能，当资源成为瓶颈，或遇到端侧设备的大量接入，需要通过流控对系统做保护。IoT场景最重要的是针对端侧连接数的流控
   netty中增加FlowControlChannelHandler，添加到pipeline靠前的位置(读从headContext到tailContext)，继承channelActive，创建TCP链路后，执行流控逻辑，
   若达到阈值，则拒绝该链接，调用ChannelHandlerContext.close关闭连接

TLS/SSL的连接数流控，可以再握手成功后，监听握手成功的事件，执行流控逻辑。握手成功后发送SslHandshakeCompletionEvent事件(SslHandler类)
FlowControlChannelHandler继承userEventTriggered，拦截TLS/SSL握手成功事件，执行流控逻辑。

10. jvm相关性能优化
    GC调优
    a.确定GC优化目标
    GC有三个主要指标
    1)吞吐量：在不考虑GC引起的停顿时间或内存消耗时，吞吐量是GC能支撑应用程序达到的最高性能指标  --gc和业务占用cpu的时间占比，尽量少则吞吐量高
    2)延时：由于GC引起的停顿时间，优化目标是缩短延迟时间或完全消除停顿(STW)，避免应用程序在运行中发生抖动。--由于stw导致业务响应产生延时
    3)内存占用：GC正常时占用的内存量

gc调优三个基本原则:
1)MInor GC回收原则：每次新生代GC要尽可能回收多的内存，减少应用程序发生full gc的频率
2)GC内存最大化原则：垃圾收集器能使用的内存越大，垃圾收集的效率越高，应用程序运行也越流畅，
但过大的内存一次full gc耗时可能较长，若能有效避免full gc就需要做精细化调优
3)3选2原则：吞吐量、延迟和内存占用不能兼得，无法同时做到吞吐量和暂停时间都最优，需要根据业务场景做选择。
对于多数IoT应用，吞吐量优先，其次是延时。  --先接收，再处理，延时次重要
对于时延敏感型的业务，需要调整次序  --延时重要，必须减少gc占用时间

b.确定服务端内存占用
优化gc之前，需要确定应用程序的内存占用大小，以便为应用和层序设置合适的内存，提升gc效率。
内存占用与活跃数据有关，活跃数据指应用程序稳定运行时长时间存活的java对象。
活跃数据的计算方式：通过gc日志采集gc数据，获取应用程序稳定时老年代占用的java堆大小，以及永久代占用的java堆大小，两者之和。

gc数据的采集
1)通过-XX:+PrintGC、-XX:+PrintGCDetails、-XX:PrintGCDateStamps等参数打印GC日志。
2)通过visulagc工具监控gc数据

gc数据解读
[GC (Allocation Failure) [ParNew: 367523K->1293K(410432K), 0.0023988 secs] 522739K->156516K(1322496K), 0.0025301 secs] [Times: user=0.04 sys=0.00, real=0.01 secs]
新生代内存分配失败，触发一次yonggc
新生代之前内存占用
新生代gc之后内存占用
新生代总内存大小
新生代gc之前堆内存大小
新生代gc后整个堆内存使用的空间大小
整个堆内存占用总空间
real本次gc占用时间

java堆大小设置原则
空间 VM命令参数  占用比例
java堆  -Xms/-Xmx  3~4倍full gc后的老年代空间占用
新生代  -Xmn  1~1.5被full gc后的老年代空间占用  ?是不是新生代gc后的占用?
老年代  堆大小-新生代  2~3被fullgc后的老年代空间占用
永久代  -XX:PermSize/-XX:MaxPermSize  1.2~1.5倍fullgc后的永久代空间占用
根据gc统计数据和应用性能测试结果进行相应的优化


垃圾收集器的选择：
若jdk8及以上，建议用G1，若较低JDK，可以使用ParNew+CMS
CMS吞吐量调优策略：
a.增加新生代空间，降低新生代gc频率，减少固定时间内新生代gc的次数
b.增加老年代空间，降低cms的频率并减少内存碎片，最终减小并发模式失效引起的fullgc发生的概率
c.调整新生代eden和survivor空间的大小比例，减少由新生代晋升到老年代的对象数据，降低cms gc频率

g1调优策略
a.不要用-Xmn选型或-XX:NewRatio等其他相关选型显示设置年轻代的大小，这样会覆盖暂停时间指标
b.暂停时间不要设置得太小，否则为了达到暂停时间目标会增加垃圾回收的开销，影响吞吐量指标
c.放置触发fullgc：在某些情况下，如并发模式失败，g1会触发fullgc，这时g1会退化使用Serial收集器完成垃圾清理工作，仅使用单线程完成gc，gc暂停时间可能达到秒级


gc调优误区
a.生产环境中不配置gc日志打印参数，担心影响业务性能
b.GC日志格式选择-XX:+PrintGCTimeStamps，导致gc日志很难跟其他业务日志对应起来
c.gc日志文件路径设置为静态路径，如gc.log，没有配置环绕、切换策略，导致重启后日志被覆盖
d.gc日志文件没有配置单个文件大小、环绕和备份机制，导致单个gc文件过大
e.只有fullgc彩会导致应用暂停，分析stw问题时直接用fullgc关键字搜索，其他的不看
f.给出最优的gc参数或者明确gc优化方向后，通过调整一次能解决问题
g.业务内存能使用不当导致性能问题，希望通过gc参数优化解决问题，业务不用改代码
h.只有gc才会导致应用暂停

其他优化手段
测试时，可能需要现在测试环境模拟海量端侧设备的接入，为了方便测试，节约机器资源，需要在客户端配置虚拟IP:
ifconfig eth0:1 XX.XX.XX.XX netmask 255.255.255.0
配置后，通过ifconfig etho0:1查看IP与子网掩码是否这个呢却，若无问题可以通过ifconfig eth0:1 up启动新增的虚拟ip进行测试
操作系统对单个ip是由连接数限制的，单个ip对应的端口范围为0~65535，0~1023被系统占用，所以连接能够分配的端口从1024开始，考虑到其他进程的端口占用，
单个ip能够接入的连接数约为6万个。若系统支持多网卡，可以采用多网卡、多IP的方式解决，否则需要使用虚拟IP的方式解决连接数限制的问题

除了通过操作系统内核参数、netty框架和jvm调优来提升单节点处理性能，还可以通过分布式集群的方式提升整个服务端的处理能力，把性能的压力分散到各个节点上。
除了可以降低单个节点的风险，也可以利用云平台的弹性伸缩实现服务端的快速扩容，以应对突发的流量洪峰。
若每个节点负担过重，一旦某个节点宕机，流量会瞬间转移到其他节点，导致其他节点超负荷，系统的可靠性降低。
通过分布式+弹性伸缩，构建可平滑扩容的服务端



不能机械地以消除静态告警或错误为目标，要结合代码上下文和业务场景进行修改，放置音修改不当引起的性能问题，以及其他问题

Edge Service性能严重下降问题
问题：基于netty4.1构建的服务新版本性能测试，Restful透传场景，性能相比上一个版本下降50%，无法满足业务的性能需求，需要定位问题原因并优化平台性能
热点代码分析(CPU执行时间维度)：调用树-方法
通过方法栈调用分析，发现平台在获取Restful请求消息体时，调用了字节数组拷贝方法，成为性能热点和瓶颈点。
上一版本中，获取请求消息体时直接返回的就是body对象，没有额外做内存拷贝，新版本为什么要修改原有的实现方式？
查看git记录，沟通过后，这段代码修改的原因是因为静态检查报错。
静态检查报错的原因：若直接返回body对象，这个对象可能会被引用者修改，为了防止被非法或意外修改，所以需要每次获取时拷贝一个新的字节数组。

频繁、大量地创建生命周期较短的字节数组对象仍然会给系统代码来巨大的压力，gc和cpu资源的额外损耗会带来性能问题。
性能测试几分钟，采集gc监控数据，查看新生代gc，以及耗时，cpu监控数据，gc占用cpu多少资源，

问题反思和改进
1.对于工具和规则不要盲从，需要结合业务场景辩证地看待，不是为了完成任务和指标而盲目修改
2.对java的克隆、对象拷贝等需要熟悉，加强基本功
4.针对某一类问题应该由某一个人统一修改，要对上下文理解。
5.对关键路径代码的修改，除了做功能测试，还需要增加性测试用例，如每天CI/CO构建的时候跑一下基础性能用例，可以更造地发现性能问题


大部分场景下，克隆的目的是修改克隆对象后不影响原对象。
要对业务运行态的关键代码路径及上下文场景清楚。
任何涉及性能的修改一定要谨慎，修改之后需要结合业务场景做相应的性能测试，以验证修改是否合理


netty的对象拷贝实现策略：
ByteBuf提供多个接口用于创建某个ByteBuf的视图或复制ByteBuf
1.duplicate:返回当前ByteBuf的拷贝对象，与原ByteBuf共享缓冲区内容，只是各自有不同的独立读写索引。
2.copy:拷贝一个新的ByteBuf对象，内容和索引都是独立的
3.copy(index,length):从指定的索引开始拷贝，拷贝的字节长度为length，内容和索引都是独立的
4.slice:返回当前ByteBuf的可读子缓冲区(readerIndex~writerIndex)，共享内容，读写索引独立维护，不修改原有的readerIndex和writerIndex
5.slice(index,length):返回当前ByteBuf的可读子缓冲区(index~index+length)，共享内容，读写索引独立维护，不修改原有的readerIndex和writerIndex



时延毛刺排查相关问题
问题：业务高峰期，偶现服务调用时延毛刺问题，时延突然增大的服务没有固定规律，问题发生的比例虽然低，但对客户的体验影响很大
分析：
服务调用时延增大，但并不是异常，因此运行日志不会打印错误(ERROR)日志。运用分布式消息跟踪系统，进行分布式环境的问题定位。
通过对服务调用时延进行排序和过滤，找出时延增大的服务调用链详细信息，发现业务服务端处理很快，但是消费者统计数据却显示服务端处理非常慢，调用链两端看的数据不一致，为何？
对调用链的详情进行分析，发现服务端打印的时延是业务服务接口调用的耗时，并不包含：
1.服务端读取请求消息、解码消息、以及内部消息投递、在线程池消息队列排队等时间
2.响应消息编码时间、消息队列发送排队时间及消息写入socket发送缓冲区的时间

服务端的处理耗时应该包括：
1.业务服务自身的调用耗时
2.服务框架的处理时间：
a.请求消息的解码(反序列化)时间
b.请求消息在业务线程池中排队等待执行时间
c.响应消息编码(序列化)时间
d.响应消息ByteBuf在发送队列中的排队时间

由于服务端调用链只采集了业务服务接口的调用耗时，没有包含服务框架本身的调度和处理时间，导致无法对问题进行定位。
服务端没有统计服务框架的处理时间，因此不排除消息发送队列或业务线程池队列积压而导致时延变大

服务调用链改进
1.包含客户端和服务端消息编解码耗时
2.包含请求和应答消息在队列中的排队时间
3.包含应答消息在通信线程发送队列(数组)中的排队时间

为方便定位问题，增加打印输出netty的性能统计日志：
1.当前系统的总链路数，以及每个链路的状态
2.每条链路接收的总字节数、周期T接收的字节数、消息接收吞吐量
3.每条链路发送的总字节数、周期T发送的字节数、消息发送吞吐量

上线运行一段时间，通过分析netty性能统计日志、调用链日志，发现双方的数据并不一致，netty性能统计的数据与前端门户看到的也不一样，
因此怀疑新增的性能统计功能存在缺陷，需继续对问题进行定位

是同步的思维问题，
netty，所有的网络I/O操作都是异步执行的，调用Channel.write并不代表消息真正发送到TCP缓冲区，所以在调用write方法后对发送的字节数做统计是不准确的
调用writeAndFlush并不代表消息已经发送到网络上，它仅是一个一步的消息发送操作，之后netty会执行一系列操作，最终发送到网络上
writeAndFlush------>ChannelOutboundBuffer-->NioSocketChannel-->SocketChannel
用户线程								NioEventLoop线程执行消息发送

通过分析writeAndFlush，发现性能统计代码忽略几个耗时：
1.业务ChannelHandler的执行时间
2.被异步封装的WriteTask/WriteAndFlushTask在NioEventLoop任务队列中的排队时间
3.ByteBuf在ChannelOutboundBuffer队列中的排队时间
4.JDK NIO类库将ByteBuffer写入网络的时间
因此统计出来的发送速度比实际会高一些。


正确的消息发送速度性能统计策略：
1.调用writeAndFlush后获的ChannelFuture
2.新增消息发送ChannelFutureListener并注册到ChannelFuture，监听消息发送结果，若写入SocketChannel成功，
则netty会回调ChannelFutureListener.operationComplete并在此进行性能统计

对netty消息发送源码分析，当发送的字节数大于0时，进行ByteBuf的清理工作，NioSocketChannel.doWrite
分析ChannelOutboundBuffer.removeBytes，将发送的字节与当前ByteBuf可读的字节数进行对比，判断当前的ByteBuf是否发送完成，若完成调用remove，否则只更新发送进度
当调用remove时，最终会调用消息发送ChannelPromise.trySuccess，通知监听消息已经完成发送
经过以上分析，调用write/writeAndFlush不代表消息已经发送完成，只有监听write/writeAndFlush操作结果，在异步回调监听中计数，结果才更精确
注意：异步回调通知由netty的NioEventLoop线程执行，需要考虑多线程并发安全问题。

若消息报文比较大，或一次批量发送的消息比较多，可能出现写半包的问题，即一个消息无法再一次write操作中全部完成发送，可能只发送了一半，
可以创建GenericProgressiveFutureListener用于实时监听消息发送进度，做更精准的统计

定位问题后，按照正确的做法对netty性能统计代码修正，上线后结合调用链日志，定位出业务高峰期偶遇的部分服务时延毛刺较大问题，优化业务线程池参数配置，解决。


常见性能统计误区
1.调用write/writeAndFlush方法后就开始统计发送速度
2.消息编码时进行性能统计：编码之后，获取out可读的字节数，然后累加。编码完成并不代表消息被写入SocketChannel，因此性能统计也不准确


netty关键性能指标采集策略
除了发送速度，还有
1.netty I/O线程池性能指标
netty I/O线程池除了负责网络I/O消息的读写，还需要同时处理普通任务和定时任务，因此消息队列积压的任务个数是衡量nettyI/O线程池工作负载的重要指标。
由于nettyNIO线程池采用的是一个线程池/组包含多个单线程的线程池机制，所以不需要像原生jdk线程池那样统计工作线程数、最大线程数等指标如ServiceTraceServerHandlerv2

netty发送队列积压消息数
此指标可以反映网络速度、通信对端的读取速度及自身的发送速度等，因此对服务调用时延的精细化分析，对于问题的定位有帮助

netty消息读取速度性能统计
针对某个Channel的消息读取速度性能统计，可以在解码ChannelHandler之前添加一个性能统计ChannelHandler，用来对读取速度进行计数，ServiceTraceProfileServiceHandler



gRPC之多语言的RPC框架，java版采用netty+protocol buffer构建。
RPC框架几个核心技术点：
1.远程服务提供者以某种形式提供服务调用相关信息，不限于服务接口定义、数据结构，及中间态的服务定义文件，如gRPC的proto文件、WS-RPC的WSDL文件定义，也可以是服务端的接口
说明文档。
调用者通过一定途径获取远程服务调用相关信息，如服务端接口定义jar包导入、获取服务端IDL文件等
2.远程代理对象：服务调者调用的服务实际是远程服务的本地代理，对于java，用的是jdk的动态代理，拦截，将本地调用封装成远程服务调用
3.通信：RPC框架与具体的协议无关，如Spring的远程调用支持HTTP Invoke、RMI Invoke，MessagePack使用的是私有的二进制压缩协议，支持多种可插拔的概念
4.序列化：远程通信需要对象转换成二进制码流进行网络传输，不同的序列化框架支持的数据类型、数据包大小、异常类型及性能等都不同。
不同RPC框架的应用场景不同，技术选择也不同。比较好的RPC框架支持多种序列化方式，甚至用户自定义序列化框架(Hadoop Avro)



为什么选择HTTP/2
传统的HTTP/1.0或HTTP/1.1(非webSocket等)是无状态的，创建HTTP连接之后，客户端发送请求消息，等待服务端响应，接收到服务端响应后，客户端接着发送后续的请求消息，
服务端再返回响应，周而复始。请求和响应都是成对出现，采用一请求对应一响应模式。某时刻，一个HTTP连接上只能单相地处理一个消息，像单行道，一个消息处理得慢，很容
导致后续消息的阻塞。

缺点如下：
1.单个链接的通信效率不高，无法多路复用
2.一个请求消息处理得慢，很容易阻塞后续其他请求此消息。
3.若客户端读取响应超时，由于消息是无状态的，只能关闭连接，重建连接之后再发送请求。若频繁地发生客户端超时，会发生大量的HTTP了解断链和重连，若采用HTTPS、SSL
链路的重建成本很高，容易导致服务端因负载过重而宕机
4.为了解决单个HTTP连接性能不足问题，只能创建一个大的连接池，在大规模集群组网场景下，HTTP连接数会非常多，额外占用大量句柄资源

采用HTTP/2后，可以实现多路复用，客户端可以连续发送多个请求，服务端也可以返回一个或多个响应，还可以主动推送数据，实现双向通信，达到TCP私有协议的通信效果
HTTP/2还支持消息压缩、服务端推送等功能，性能提升很多。


netty http/2服务端创建原理和源码分析
构建NettyServer--start，创建nettyServerTransport，通过其创建grpc的netty http/2 ChannelHandler实例，放入ChannelPipeline(grpcHandler和negotiationHandler)--
NettyServerHandler是gprc用来处理http/2的channelhandler，从类继承关系看，其负责http/2消息的处理，如http/2请求消息体和消息头读取、frame消息的发送、stream状态消息
的处理等。
构建ProtocolNegotiator实例，用于http/2连接创建的协商。
PlaintextUpgradeNegotiator通过设值Http2ClientUpgradeCodec用于101协商和协议升级。


服务端接收http2/请求消息原理和源码分析
grpc通过集成Http2FrameAdapter将自定义的FrameListener添加到netty的Http2ConnectionDecoder中，在http/2请求消息头和消息被解析成功后，回调grpc的FrameListener，
接收并处理Http/2请求消息
grpc注册FrameListener代码在NettyServerHandler中decoder().frameListener(new FrameListener())
读取头和体时调用onHeadersRead/OnDataRead
http/2消息头的读取和处理流程：
1.netty http/2协议栈完成消息头的解码后，触发grpc注册的FrameListener最终调用NettyServerHandler.onHeadersRead
2.对http/2头的Content-Type做校验
3.从http头的url中提取目标接口和方法名，如method为helloworld.Greeter/SayHello，替代码在NettyServerHandler.determineMethod
4.根据streamId获取缓存的netty Http2Stream对象，若为空，则说明streamId非法异常，NettyServerHandler.requireHttp2Stream
5.将netty的http2消息头转成grpc的内部metadata，convertHeaders
6.创建nettyServerStream，聚合Sink、TransporState等类，通过transportListener.streamCreated对消息头处理(NettyServerHandler.onHeadersRead)

http/2消息体的处理
入口是FrameListener，调用NettyServerHandler.onDataRead
流程：
1.对http/2的flow control pinging和flow contorl window updates消息处理，(FlowControlPinger.onDataRead)
2.根据streamid获取Http2Stream，根据Http2Stream的Http2Connection.PropertyKey获取NettyServerStream的TransportState类，触发grpc的消息读取流程(NettyServerHandler.onDataRead)
3.将netty的请求消息体ByteBuf转换成grpc内部的nettyReadableBuffer对象，(TransportState.inboundDataReceived)
4.调用deframe，完成请求消息的解码(AbstractStream2.deframe)


服务端发送http/2响应消息原理和源码分析
grpc服务端通过将响应消息封装成WriteQueue.AbstractQueuedCommand，异步写入WriteQueue，然后调用WriteQueue.scheduleFlush，将响应消息发送命令放到NioEventLoop中执行，
调用channel.write将其发送到ChannelPipeline，由grpc的NettyServerHandler拦截write方法，按照命令的分类进行处理，最后调用NettyHttp2ConnectionEncoder.writeXXX完成发送
关键流程：
1.通过NettyServerStream的Sink类对需要发送的http/2响应消息封装成task，实现消息发送异步化。(NettyServerStream.Sink.writeHeaders)
2.WriteQueue将任务投递到对应的Channel的NioEventLoop线程异步执行发送(WriteQueue.scheduleFlush)
3.NioEventLoop线程循环处理待发送的响应，调用Channel.write，将消息发送到ChannelPipeline，由grpcChannleHandler拦截(WriteQueue.flush)
4.grpc的NettyServerHandler拦截write方法， 按照命令的类型进行分类处理，



grpc netty http/2客户端工作机制
netty http/2客户端创建原理和源码分析
grpc http/2协议栈(客户端)主要实现：NettyClientTransport和NettyClientHandler
关键流程：
1.NettyClientTransport.start创建NettyClientHander
2.通过NettyClientHander创建http/2协商类negotiationHandler
3.创建客户端Bootstrap，发起http/2连接(NettyClientTransport.write)
4.netty的NioSocketChannel初始化并向Selector注册之后(发起http连接之前)，立即由nettyClientHandler创建WriteQueue，用于接收并处理grpc各种命令，如
链路关闭指令、发送Frame指令、发送Ping等。

grpc netty http/2客户端的创建与传统客户端有些差异：
1.创建NettyClientHandler(包装成ProtocolNegotiator.Handler，用于http/2的握手协商)之后，不是由传统的ChannelInitializer在初始化Channel时将
NettyClientHandler加入Pipeline，而是直接通过Bottstrap的Handler加入piepieline以便立即接收和发送任务
2.客户端使用的work线程组并非EventLoopGroup，而是一个Eventloop，即HTTP/2客户端使用的worker线程并非一组线程(cpu核数*2)而是一个EventLoop线程，
一个NioEventLoop线程可以同时处理多个HTTP/2的客户端连接，它是多路复用的，对于单个HTTP/2客户端，若默认独占一个worker线程组，将造成极大浪费，也可能局并移除


客户端发送http/2请求消息原理和源码分析
grpc默认基于netty http/2+protocol buffer进行rpc调用
关键技术点：
1.客户端发送HTTP/2请求消息头，将grpc内部metadata格式消息头转换成netty的Http2Headers，封装和曾CreateStreamCommand，发送到WriteQueue，(NettyClientStream.Sink.writeHeaders)
2.将请求消息封装成SendGrpcFrameCommand发送到WriteQueee，(NettyClientStream.Sink.writeFrame)
3.nettyClientHandler拦截write方法，分别处理CreateStreamCommand和SendGrpcFrameCommand，(NettyClientHandler.write)
4.发送客户端请求消息头时，要创建NettyClientStream，通过自增的方式生成streamId，后创建NettyClientStream的TransportState对象，(NettyClientHandler.createStream)
5.调用netty Http2ConnectionEncoder的writeHeaders和writeData完成请求头和体的发送


客户端接收HTTP/2响应消息原理和源码实现
入口在NetyClientHandler
关键点：
1.nettyClientHandler的onHeadersRead被调用两次，根据endStream判断是否是Stream的结尾
2.请求和相应的映射关系：根据streamId可以关联同一个HTTP/2 Stream，将NettyClientStream缓存到Stream，客户端可以再接收到响应消息头或消息体时还原NettyClientStream，后续处理
3.客户端和服务端的HTTP/2Header和Data Frame解析通用同一方法即MessageDeframer.deliver


消息的序列化原理和源码分析
grpc在将消息发送到WriteQueue之前，会调用requestMarshaller将消息序列化成InputStream，ClientCallImpl.sendMessage，之后调用stream.writeMessage消息发送

反序列化原理和源码分析
netty通过grpc注册的FrameListener回调nettyClientHandler和NettyServerHandler，对消息进行处理。如服务端请求消息反序列化，由grpc的SerializingExecutor
负责消息体的解析（JumpToApplicationThreadServerStreamListener.messageRead，最终调用ProtoLiteUtils.Marshaller，通过parse


影响RPC框架性能的三个核心要素
1.I/O：用什么样的I/O方式将数据发送，BIO、NIO、AOI，I/O模型在很大程度上决定框架通信的性能
2.协议：采用什么样的通信协议，公有、私有、JSON还是其他二进制序列化框架，决定了消息的传输效率
3.线程模型：消息读取后的编解码在那个线程中执行，编解码后的消息如何派发，通信模型不同，对性能的影响也不同，对性能影响最大

HTTP/2服务端创建、HTTP/2请求消息的接入和响应发送都由Netty NioEventLoop线程负责，
grpc消息的序列化和反序列化业务服务接口的调用由grpc的SerializingExecutor线程池负责

客户端调用涉及线程(以同步阻塞调用为例)：
1.业务线程，负责调用grpc服务端的并获取响应，请求消息的序列化
2.grpc-default-dexcutor线程池负责：客户端负载均衡策略及netty客户端的创建
3.Netty NioEventLoop线程负责:http/2客户端的连接创建、网络I/O数据的收发
4.SerializingExecutor负责，具体是现实ThreadlessExecutor:http/2响应消息的反序列化
5.SerializingExecutor调用responseFuture.set(result)唤醒阻塞的业务线程继续执行，完成同步阻塞的RPC调用

线程模型总结：
消息的序列化和反序列化由grpc线程负责，没有在netty的Handler中做CodeC，可以降低nettyI/O线程的工作负载提升系统的吞吐量
grpc采用的是网络I/O线程和业务调用线程分离策略，大部分场景最优。但对于那些接口的逻辑非常简单，执行时间很短，不需要与外部网元交互、访问数库或本地存储，也不需要
等待其他同步操作的场景，建议直接在Netty I/O线程中调用接口，不需要再发送到后端的业务线程池，避免线程上下文切换，也消除并发问题
当前netty NIO线程和grpc的SerializingExecutor之间没有映射，当线程数量比较多时，锁竞争会非常激烈，可以采用I/O线程和grpc服务调用的线程绑定方式，降低出现
锁竞争的概率，提升并发性能




channelReadComplete方法被多次调用问题
问题：基于http的服务器，生产环境中运行一段时间，部分消息逻辑处理错误，但在灰度测试环境中无法重现问题。
生产环境中将某个服务实例的调用日志打开一段时间，以便定位问题。发现对于同一个http请求消息，当发生问题时，业务channelHandler的channelReadComplete会被调用多次，
但大部分消息都调用一次，按照业务的设计，当服务端读到一个完整http请求消息时，在channelReadComplete中进行业务处理。多多次则出现错误
通过对客户端请求消息和netty框架进行源码分析找到问题：tcp底层并不了解上层业务数据的具体含义，会根据tcp缓冲区的实际情况进行包的拆分，所以在业务上认为一个完整的
http报文可能会被tcp拆分成多个包发送，也可能把多个小的包封装成一个大的数据包发送。
导致数据包拆分和重组的原因：
1.应用程序写入的字节大小大于套接字发送缓冲区大小
2.进行MSS大小的TCP分段
3.以太网的有效载荷(payload)大于MTU的IP分片
4.开启了TCP Nagle算法

由于底层的TCP无法理解上层的业务数据，所以在底层无法保证数据包不被拆分和种族，只能通过上层应用协议栈设计来解决，方式：
1.消息定长，如每个报文的大小固定为200字节，若不够，空位补空格
2.在包尾增加换行符(或其他分隔符)进行分隔，如FTP
3.将消息分为消息头和消息体，消息头包含表示消息总长度(或消息体长度)的字段，通常消息头的第一个字段用int32表示消息的总长度

对于http请求消息，当业务并发量大时，无法保证一个完整的http消息会被一次全部读取到服务端。当采用chunked方式编码时，http报文也是分段发送的，此时服务端读取的
也不是完整的http报文。
netty提供了HttpObjectAggregator，保证后端业务ChannelHandler接受的是一个完整的http报文，

HTTPObjectAggregate可以保证netty读到完整的http请求报文后才调用一次业务channelHandler的channelRead方法，无论这条报文底层经过几次SocketChannel的read调用。
但channelReadComplete并不是在业务语义上的读取消息完成后被触发，而是每次从SocketChannel成功读到消息后，由系统触发，即若一个http消息被tcp协议栈发送n次，则服务端的
channelReadComplete会被调用n次。
灰度测试环境中，由于客户端并没有采用chunked编码方式，并发压力也不大，所以一直没有发现该问题，到了生产环境中有些客户端采用了chunked方式发送http请求，客户端并发量也
比较大，出发了服务端的问题



ChannelHandler使用的一些误区

channelReadComplete方法调用，只要对应的SocketChannel成功读取到了ByteBuf，就会被触发(NioByteUnsafe.read)
大部分解码协议器，如Netty的ByteToMessageDecoder，会调用的具体协议解码器对ByteBuf解码，只有解码成功，才会调用后续的ChannelHandler.channelRead方法
ByteToMessageDecoder.fireChannelRead
而channelReadComplete属于透传调用，无论是否有完整的消息被解码成功，只要读到消息，都会触发后续的channelReadComplete(ByteToMessageDecoder.channelReadComple)


ChannelHandler职责连调用
ChannelPipeline以链表方式管理某个Channel对应的所有ChannelHandler，下一个ChannelHandler的触发需要在当前ChannelHandler中显示调用，如firexxx
TailContext有时执行一些系统性的清理操作，如当channelRead执行完成时，将请求消息(如ByteBuf)释放，
防止因为业务遗漏释放而导致内存泄漏(内存池模式)(TailContext.onUnhandledInboundMessage)




流量整形(traffic shaping)一种主动调整流量输出速度的措施。典型应用是基于下游网络节点的TPS指标控制本地流量的输出。
与流量控制的区别在于：流量整形是对流量控制中需要丢弃的报文进行缓存。当令牌桶有足够多的令牌时，在均匀地向外发送这些被缓存的报文。流控则直接丢弃
整形可能增加延时，而流控不引入额外的延时


netty内置了三种流量整形：
1.单链路的：ChannelTraffciShapingHandler，某个链路
2.全局：GlobalTrafficShapping，整个进程
3.全局和单个链路综合：GlobalChannelTraffixShappingHandler

流量整形主要作用：
1.防止由于上、下游网元性能不均衡导致下游网元被压垮，业务流程中断
2.防止由于通信模块接收消息过快，后端业务线程处理不及时，导致出现“撑死”问题



netty流量整形工作机制
原理：拦截channelRead和write，计算当前需要发送的消息大小，对读取和发送阈值判断，若达到则暂停读取和发送，待下一个周期继续处理，以实现在某个周期内对消息读写速度进行控制


流量整形工作原理和源码分析
1.消息读取流量整形，ChannelTrafficShapingHandler
a.解码之前拦截channelRead，计算读取的ByteBuf大小，AbstractTrafficShapingHandler.calculateSize
b.计算需要暂停读取消息的等待时间(TraffciCounter.readTimeToWait)
c.满足整形条件，修改channel的状态为非自动读取，将READ_SUSPENDED修改为false，channel进入整形状态，不再从tcp缓冲区读取请求消息(AbstractTrafficShapingHandler.channelRead)
d.创建恢复Channel为可读的定时任务，由Channel对应的NioEventLoop执行
e.达到暂停读取时间后，触发定时任务，重新修改channel的READ_SUSPENDED=TRUE,将autoRead=true(ReopenReadTimerTask.run)


2.消息发送的流量整形
a.计算需要暂停发送的等待时间(TrafficCounter.writeTimeToWait)
b.若需要暂停发送时间大于10ms，通过定义任务进行消息发送，否则直接封装成功ToSend任务，添加到NioEventLoop的task队列立即执行。(AbstractTrafficShapingHandler.write)
c.由channel对应的NioEventLoop从messagesQueue中循环取待发送的toSend任务，执行消息发送操作(ChannelTrafficShapingHandler.sendAllValid)


并发编程在流量整形中的应用
1.volatile的使用，java提供的最轻量级的同步机制，java内存模型对volatile专门定义规则特性：
a.线程可见性：一个线程修改了变量后，无论是否加锁，其他线程都可以立即看到最新的修改
b.禁止指令重排徐优化：普通的变量仅保证在方法的执行过程中所有依赖赋值结果的地方都能获取正确的结果，而不能保证变量赋值操作的顺序与程序代码的执行顺序一致
c.减小锁范围：由于多线程调用，需要对消息队列ArrayDeque<ToSend>加锁，但不需要对后续的其他操作加锁。如通过NioEventLoop.schedule方法执行定时任务，因为
它本身并发安全，所以不需要额外加锁(ChannelTrafficShapingHandler.submitWrite)
d.原子类：由于支持对所有的Channel做流量整形，不同的Channel会绑定不同的NioEventLoop线程，所以消息发送和读取的计算都是并发操作，需要同步保护
若用同步关键字，主要问题是进行线程阻塞和唤醒带来的性能额外损耗，阻塞同步，悲观锁。随着硬件和操作系统指令集的发展，产生非阻塞同步，乐观锁。CAS

流量整形参数调整不要过于频繁
虽然通过AbstractTrafficShapingHandler.configure可以动态调整整形的读写速度和检查周期，但由于调整后需要对一些统计数据进行重新设置和重新计算，在下一个
周期才生效。

在channel关闭或者流量整形channelHandler被移除时，由于ChannelTrafficShapingHandler持有消息发送队列，若不对消息队列进行清空处理，会导致发送消息丢失，
或者消息队列积压，引起内存泄露。netty框架当连接关闭时，回调用handlerRemoved，将待发送的消息全部释放，防止内存泄漏(ChannelTrafficShapingHandler)

注意对客户端流量整形时，原理是将待发送的消息封装成task放入消息对了，等待执行时间到达后继续发送，若业务发送线程不判断Channel的可写状态，可能导致oom



SSL安全特性
1.单向认证(客户端验证服务端的合法性)：原理
a.SSL客户端向服务端传送客户端SSL协议的版本号、支持的加密算法种类、产生的随机数及其他可选信息
b.服务端返回握手应答，向客户端传送确认SSL协议的版本号、加密算法的种类、随机数及其他信息
c.服务端向客户端发送自己的公钥
d.客户端对服务端的证书进行认证，服务端的合法性校验包括证书是否过期、发型服务器证书的CA是否可靠、发行者证书的公钥能够正确解开服务器证书的“发行者的数字签名”、
服务器证书上的域名是否与服务器的实际域名匹配等。
e.客户端随机产生一个用于后面通信的“对称密码”，用服务端的公钥对其加密，将加密后的“预备主密码”传输给服务器
f.服务端用自己的私钥解开加密的“预备主密码”，通过一系列步骤来产生主密码
g.客户端向服务端发出信息，指明后面的数据通信将使用主密码作为对称秘钥，同时通知服务器客户端的握手过程结束
h.服务端向客户端发出信息，指明后面的数据通信将使用主密码作为对称秘钥，同时通知客户端服务器端的握手过程结束
SSL握手部分结束，SSL安全通道建立，客户端和服务端开始使用相同的对称秘钥对数据加密通过socket传输

2.双向认证
比单向认证多了一步，服务端发送认证请求消息给客户端，客户端发送自签名整数给服务端进行安全认证

CA认证
使用JDK keytool工具生成的数字证书是自签名的，自签名是指证书只能保证自己是完整且没有经过非法修改的，但无法保证这个证书是属于谁的。
为了对自签名证书进行认证，需要每个客户端和服务端都交换自己自签名的私有证书，对大型网站或应用服务器来说，工作量巨大。
基于自签名的SSL双向认证，只要客户端或服务端修改了秘钥的证书，就需要重新进行签名和证书交换。一次在实际的商用系统中，会通过第三方CA证书
颁发结构进行签名和验证。如浏览器保存的常用CA_ROOT，每次连接到网站时只要这个网站的证书是经过这些CA_ROOT签名的就可以通过验证

netty提供两种SSL实现机制
1.JSK SSL:JdkSslEngine，对JDK原生的SSL功能包装和增强
2.OpenSSL:OpenSslEngine，通过原生的方式调用OpenSsl，提升性能
-Dio.netty.handler.ssl.noOpenSsl开关OpenSsl。


netty客户端SSL握手超时的问题
问题：业务基于netty开发了服务端和客户端，采用TCP私有协议通信，业务运行正常。后来采用TSL通信，客户端超时。
握手超时原因定位：
客户端通过TLS连接服务端后发送请求消息，没有响应，考虑双方的SSL握手是否成功，通过抓包或打印SSL调试日志查看我收过程。
客户端开始SSL调测参数：-Djavax.net.debug=ssl,handshake,data,trustmanager
重启客户端，看握手日志，发现客户端发送完***ClinetHello,TLSv1.2后，没有收到服务端的响应
排查服务端代码，发现服务端添加SslHandler位置不对，把他放在了业务解码器后面，这样SSL握手消息先会被业务解码器调用，不满足业务解码器的解析条件，握手消息不会调用到
后面的SslHandler，所以握手失败

若用JDK的SSL引擎，可以通过vm启动参数-Djavax.net.debug=ssl,handshake,data,trustmanager打印SSL握手日志
若用OpenSSL引擎，则可以通过抓包定位问题

常见SSL握手失败原因：
1.证书不匹配，证书校验失败
2.证书过期
3.双方SSL引擎支持的TSL/SSL版本不一致，有些SSL引擎不支持高版本的TSL/SSL
4.域名校验失败
5.双方支持的加密算法不一致，导致算法套件匹配失败


SSL握手性能问题
问题：服务端采用netty构建，业务高峰期会出现客户端批量超时问题，超时后又有大量客户端使用SSL接入，导致服务端的CPU使用较高，部分客户端接入超时
SSL握手性能热点分析
模拟生产环境进行压测，不断connect。
对Netty SSL服务端CPU热点方法进行采集，服务端SSL握手热点方法。对SecureChatSslContextFactory.getServiceContext分析，每次都new FileInpuStream
而因为keystore证书通常不会变更，每当有新的SSL客户端接入就要重新加载证书，并发量高时会影响性能。可以把证书加载的文件做成缓存。


握手成功事件
监听SslHandshakeCompletionEvent事件，在SSL握手成功后做业务路基处理,如SslHandler.setHandshakeSuccess中会触发fireUserEventTriggered
业务ChannleHandler通过自定义userEventTriggered方法拦截并处理SslHandshakeCompletionEvent事件

SSL连接关闭事件
当SSL连接关闭时触发SslCloseCompletionEvent事件(SslHandler.notifyClosePromise)
用户通过userEventTriggered方法拦截并处理SslCloseCompletionEvent事件



高并发场景下，若客户端并发量过大，而服务端又没有针对并发连接数进行流控，或者没有弹性伸缩能力，很可能宕机

Netty HTTPS服务端宕机问题
a和b通过https通信，某天客户端突然发现大量超时，持续一段时间后，发送给服务端的消息全部失败，吞吐量为0，查看服务端日志，发现大量oom，初步判断内存泄漏
查看SLB入口流量，发现客户端最初发生超时时并没有明显的流量变化，排除突发流量高峰导致系统过载。
分析服务端日志发现，在客户端超时时间段，后端的缓存服务出现了问题，缓存查询耗时比客户端的超时时间还长，因此导致了客户端超时。
由于采用http/1.1，客户端超时后，需要关闭当前链路，重新发起https连接，因此日志中会出现大量打印读取响应超时和客户端关闭连接的异常

服务端内存泄露原因分析：
分析清楚故障场景后，模拟故障进行测试，dump查看服务端内存文件，分析泄漏点
NioSocketChannel内存占用排名第一，通过表格查看NioSocketChannel对象的内存占用详情，共有5729个NioSocketChannel对象，占用131MB左右内存。
通过内存占用情况分析，可以确认导致服务端内存泄露的是NioSocketChannel(Biggest Top-Level Dominator Packages)

NioSocketChannel泄露原因探究
导致NioSocketChannel泄露的可能原因：
1.代码缺陷，https客户端关闭连接后，服务端没有正确关闭连接
2.服务端负载比较重，客户端超时后的断连和重连速度超过服务端关闭连接速度，导致服务端的NioSocketChannel发生积压。随着积压数增加，导致占用内存快速增加，频繁gc
使得服务端处理更慢，积压更严重，最后导致OOM

具体定位策略：
1.通过netstat查看服务端的端口连接状态，是不是不断地创建和回收连接，还是服务端没有关闭连接导致连接一直增长
2.查看NioSocketChannel的状态，是全部没关闭，还是部分关闭、部分打开
3.停止压测一段时间，观察连接数，以及服务端的内存占用情况，看服务端是否可以自动恢复

压测过程中，动态采集HTTPS的连接状态，发现超时的连接被服务端关闭
netstat -an|grep 8443|grep "ESTABLISHED" |wc -l
接着通过OQL在内存堆栈中查询NioSocketChannel的连接状态，其中处于关闭的连接数为25
select s from io.netty.channel.socket.nio.NioSocketChannel s where s.ch.isOutputOpen = false
服务端尚未主动关闭的NioSocketChannel实例个数为5806
select s from io.netty.channel.socket.nio.NioSocketChannel s where s.ch.isOutputOpen = true
从OQL查询看出，内存中尚有被服务端关闭但没有来得及被gc回收的NioSocketChannel对象，证明客户端超时关闭连接后，服务端感知了连接关闭事件并主动关闭了连接。

当客户端发生大量超时、服务端占用内存飙升时，停止压测，观察服务端是否可以自动恢复。
可以看出，停压测后，服务端的堆内存正常回收，说明不存在由于忘记释放NioSocketChannel导致的内存泄露问题
同步观察HTTPS的连接数，发现停压测后连接数逐步下降，最终为0

经上述分析，并不是由于服务端忘记关闭NioSocketChannel导致内存泄漏，而由于服务端关闭NioSocketChannel的速度没有客户端接入速度快导致的NioSocketChannel缓慢
积压，一定数量时，无法在新生代被gc，所以达到晋升阈值后被复制到老年代，引起gc，最终导致oom


高并发场景下缺失的可靠性保护
客户端和服务端的rpc调用，对业务组网进行分析
客户端采用http连接池的方式与服务端进行rpc调用，单个客户端连接池上限为200，客户端部署30个实例，而服务端只部署了3个实例。
业务高峰期，每个服务端要处理6000(30*200)个http连接，服务端时延增大之后，导致客户端批量超时，超时后客户端会关闭连接重新发起connect操作，某个瞬间，几千个https
连接同时发起SSL握手操作，由于服务端此时也处于高负荷运行状态，导致部分连接SSL握手失败后者超时，超时后客户端继续重连，进一步加重服务端的处理压力，最终导致服务端
来不及释放客户端关闭的连接，引起NioSOcketChannel大量积压，最终oom
从客户端的运行日志可看到一些SSL握手发生了超时

对服务端业务高峰期线程堆栈分析发现，系统忙于处理SSL握手，处理客户端HTTPS连接接入是热点
服务端没有对客户端的连接数做限制，导致尽管ESTABLISHED状态的连接数并不会超过6000上限，但由于一些SSL连接握手失败，再加上积压在服务端的连接并没有及时释放，
最终导致NioSocketChannel的大量积压

客户端也没有流控机制，只要连接数不够用，就会一直创建连接，达到连接池配置的最大连接数。
正是由于客户端和服务端都没有对高并发时大量https链路断连和重连进行保护，导致服务端的oom异常，


可以从功能和架构层面优化，由于架构优化改动大，先以快速解决问题为目标提升服务端的可靠性，通过较小改动解决当前问题

netty HTTPS服务端可靠性优化
1.https并发连接数流控
服务端增加对客户端并发连接数的控制。
基于pipeline机制，可以对SSL握手成功、SSL连接关闭做切面拦截，通过流控切面接口，对HTTPS连接进行计数，根据计数器进行流控，算法：
a.获取流控阈值
b.从全局上下文获取当前的并发连接数，与流控阈值对比，若小于则++，允许
c.若等于或大于，则抛出异常给客户端
d.SSL连接关闭时，获取上下文中的并发链接数，--
注意：
a.流控的ChannelHandler声明为@ChannelHandler.Sharable，这样创建一个全局流控实例，可以再所有的SSL连接中共享
b.通过userEventTriggered方法拦截SslHandshakeCOmpletionEvent和SslCloseCompletionEvent事件，更新计数器
c.流控并不仅针对ESTABLISHED状态的http连接，而是针对所有状态的连接，因为客户端关闭连接，并不意味着服务端也关闭连接，只有触发SslCloseCompletionEvent事件是，
服务端才真正关闭了NioSocketChannel，gc才会回收连接关联的内存
d.流控ChannelHandler挥别多个NioEventLo线程调用，对计数器操作要保证并发安全。

2.切换JDK SSL引擎到OpenSSL
可以提升SSL的握手和通信性能
3.HTTPS服务端弹性伸缩
由于HTTPS服务端是无状态的，可以用公有云的弹性伸缩服务，针对服务端的cpu和内存资源占用，配置弹性伸缩策略，业务高峰期通过动态扩容方式，保证业务平稳运行。


netty HTTPS客户端优化
1.https连接池调整
由于生产环境中，客户端和服务端部署实例数是10：1，服务端采用netty构建，它的I/O通信通常不是瓶颈，若服务端返回响应慢，说明服务端处理负荷已经严重，或服务端依赖的
缓存、数据库、第三方等处理比较慢，拖累了服务端。此时若客户端一味增加https连接数，并不能改善服务端的处理性能，返回加剧他的负担，导致服务端响应更慢
将客户端的连接池上线调整为200->50，降低业务大批量超时重建https连接对服务端的冲击
2.超时时间调整
测试和线上的环境不同，若超时时间过短导致一定比例的超时失败，但由于http超时后会关闭连接，导致部分连接从长连接变成短连接，重建SSL连接本身也比较耗时
根据KPI数据分析，适当地调长了客户端超时时间，由于调长超时时间后，可能导致客户端线程同步阻塞时间变长，因此也同步调大了tomcat工作线程的大小，以减小客户端
发生同步阻塞时对系统吞吐量的影响



## 架构层面的可靠性优化
1.端到端架构问题刨铣
当前架构主要问题：
1.客户端采用同步阻塞式http调用，I/O效率不高，调用线程容易被阻塞
2.客户端采用同步阻塞式进行rpc调用，若服务端响应慢，容易阻塞调用方的线程
3.客户端和服务端采用http/1.1，由于是无状态的一请求一应答模式，所以当个链路通信效率低，需要创建大量链路来提升I/O性能

http client切换到nio
采用httpClient的问题：
1.由于I/O读写是阻塞的，所以调用线程容易被网络I/O阻塞
2.无法充分利用硬件资源，当调用线程被I/O阻塞时，CPU资源闲置，但系统的吞吐量上不来。

切换到netty的异步httpclient后，利用I/O多路复用技术，把多个I/O的阻塞复用到同一个select方法的阻塞上，使得系统在单线程的情况下可以同时处理多个客户端连接。
与传统的多线程/多进程模型比，I/O多路复用的最大优势是系统开销小，系统不需要创建新的进程或者线程，也不需要维护这些进程和线程的运行，减少了系统的维护工作量，
节省了系统资源。(HttpClient)


同步RPC调用切换到异步调用
nio只解决了通信层面的异步问题，跟服务调用的异步没有必然关系，即便采用传统的BIO通信，依然可以实现异步服务调用，只不过通信效率和可靠性比较差而已

异步服务调用和通信框架关系
用户发起远程服务调用后，经历层层业务逻辑处理、消息编码，最终序列化的消息会被放入通信框架的消息队列。
业务线程可以选择同步等待，也可以选择直接返回，通过消息队列的方式实现业务业务层和通信层的分离是比较成熟、典型的做法。
采用NIO还是BIO对上层的业务不可见，双方交点在消息队列，

同步服务调用最大缺点是一旦服务端响应慢，rpc客户端线程就会同步阻塞等待响应，之哟啊某个接口的消息慢，就会导致后续所有接口都在线程池队列中排队，整个系统可靠性和性能较差


异步服务调用设计
java8的CompletableFuture提供丰富的异步功能，带有async
流程：
1.消费者通过rpc框架调用服务端
2.netty异步发送http请求消息，若没有发发生I/O异常就正常返回
3.http请求消息发送成功后，I/O线程构造CompletabeFuture对象，设置到RPC上下文中
4.用户线程通过rpc上下文获取completableFuture对象
5.不阻塞用户线程，立即返回completableFuture对象
6.通过completableFuture编写future函数，在lambda函数中实现异步回调逻辑
7.服务端http返回响应，netty负责反序列化
8.nettty i/o线程通过调用CompletableFuture.complete将应答设置到CompletableFuture中
9.CompletableFuture通过whenCompleteAsync等方法异步执行业务回调逻辑实现rpc调用的异步化


异步服务调用优势
1.化串行为并行，提升服务调用效率，减少业务线程阻塞时间
2.化同步为异步，避免业务线程阻塞


http/1.1存在问题
若http栈采用异步非阻塞I/O模型(如netty)，则可以解决同步阻塞I/O的问题，收益：
1.同一个I/O线程可以并行处理多个客户端连接，有效减少I/O线程数量，提升资源利用率
2.读写等I/O操作都是非阻塞，不会因为服务端响应慢、网络时延长导致I/O线程被阻塞


采用非阻塞I/O的http/1.1还有性能问题：
1.由于http是无状态的，客户端发送请求后，必须等到接受服务端响应才能继续发送下一个请求，这样某一个时刻，链路上只能存在单向的消息流。把tcp的全双工模式变成了单工模式
若服务端响应耗时大，则单个http连接的通信性能验证下降，只能新建连接来提升I/O性能。但这也会有副作用：如句柄数的增加、I/O线程的负担加重。
2.超时关闭连接问题：http客户端超时后，由于协议无状态，无法对请求和响应进行关联，只能关闭连接后重连，反复地锻炼和重连会增加成本和时延。若采用传统RPC私有协议，
请求和响应和可以通过消息ID或sessionId关联，某条消息的超时并不需要关闭连接，只需丢弃超时消息即可。
3.http本身包含文本类型的协议消息头，占用一些直接。采用json等文本类序列化方式，报文相比传统的私有rpc协议也大很多，增加网络传输开销


http/2 streaming调用
若选择Restful API或HTTP作为内部rpc接口协议，建议用http/2来承载，优点包括支持双向流、消息头压缩、单TCP的多路复用、服务端推送等。可解决http/1.1的问题
基于http2的rpc调用，可以参考和借鉴grpc实现策略：
1.服务端streaming。适用场景：客户端发送单个rpc请求，服务端可能返回的是一个结果列表，服务端不想等到所有响应消息都组装完才返回一个应答消息给客户，
而是处理完成一个返回一响应，直到服务端关闭stream，通知客户单响应全部发送完
本质：多个列表，为防止块的等慢的，可以处理完一个返回一个，不需要等所有都处理完统一返回。可以压缩单个响应的时延，端到端提升用户的体验
2.客户端streaming。多用于数据汇聚和汇总计算场景。
3.都streaming。充分利用http/2的多路复用。某时刻，http/2连接上既有请求也有响应，全双工通信。




如果服务端没有考虑各种异常场景，很难稳定运行。

mqtt服务接入超时问题
问题：生产环境mqtt服务运行一段时间后，发现新的端侧设备无法接入，连接超时。
分析mqtt服务端日志，没有明显的异常，但内存占用很高，查看连接数，发现有数十万个TCP连接处于ESTABLISHED状态，实际的MQTT连接数应该在1万左右，有问题
由于mqtt服务端的内存是按照2万个左右连接数规模配置的，因此当连接数达到数十万规模后，导致服务端大量SocketChannel积压、内存暴涨、高频率GC和较长STW，
对端侧设备的接入造成很大影响，部分设备mqtt握手超时，无法接入

连接数膨胀原因分析
通过抓包分析发现，一些端侧设备并没有按照mqtt协议规范进行处理，包括：
1.客户端发起connect连接，ssl握手成功后没有按照协议规范继续处理，例如发送ping命令。
2.客户端发起tcp连接，不做SSL握手，也不做后续处理，导致TCP连接被挂起

由于服务端严格按照mqtt规范实现，上述端侧设备不按规范接入，实际上消息调度不到mqtt应用协议层。
mqtt服务端理论上依赖keep Alive机制进行超时检测，当一段时间接收不到客户端的心跳和业务消息时，会触发心跳超时，关闭连接。
但上述两种接入场景，由于mqtt的连接流程没有完成，mqtt协议栈不认为这是个合法的mqtt连接，因此心跳保护机制无法对上述tcp连接检测。
客户端和服务端都没有主动关闭这个连接，导致tcp连接一直保持。

mqtt连接建立过程：

m			--1.创建TCP连接--> 不继续握手         MQTT服务端
q     --2.SSL握手-->    不发送连接指令
t     --3.发送CONNECT消息-->           
t     <--4.发送CONNACK消息--
端    --..其他交互--
侧    --5.发送PING消息-->
设备  <--6.返回PINGRSP消息--



无效连接的关闭策略
针对这种不遵循mqtt规范的端侧设备，除了要求对方按照规范修改，服务端还需要做可靠性保护，策略：
1.端侧设备的TCP连接接入后，服务端启动一个链路检测定时器加入Channel对应的NioEventLoop。(ChannelIdleHandler.channelActive中进行创建超时任务到NioEventLoop中)
2.链路检测定时器一旦触发，就主动关闭TCP连接
3.TCP连接完成MQTT协议层的CONNECT之后，删除之前创建的链路检测定时器


生产环境修复后，平稳运行，查看MQTT连接数，稳定在1万左右，与预期一致。


基于netty的可靠性设计
从应用场景看，netty是基础的通信框架，一旦出问题，就是大问题。它的可靠性会影响整个业务集群的数据通信和交换。尤其对于分布式架构
从运行环境看，netty会面临恶劣的网络环境，要求它自身的可靠性足够好，平台能解决的可靠性需要由netty自身来解决，否则会导致上层用户关注过多的底层故障，降低netty的易用性，
同时增加用户的开发和运维成本


业务定制I/O异常
一些特殊场景下，用户可能要关心这些异常，并针对这些异常进行定制处理，如：
1.客户端的断连和重连机制
2.消息的缓存重发
3.在接口日志中详细记录故障细节
4.运维相关功能，如告警、触发邮件/短信等
netty的处理策略是，发生I/O异常时，底层的资源由它负责释放，同时将异常堆栈信息以事件的形式通知给上层用户，由用户对异常进行定制处理。(handler.exceptionCaught)


链路的有效性检测
当网络发生单通、连接被防火墙挡住、长时间gc或者通信线程发生非预期异常时，链路会不可用且不易被及时发现。
特别是若异常发生在凌晨业务低谷期间，当早晨业务高峰来时，由于链路不可用导致瞬间大批量业务失败或超时，将对系统的可靠性产生重大的威胁
从技术层面看，要解决链路的可靠性问题，必须周期性地对链路进行有效性检测。流行的是心跳检测
心跳检测机制分三个层面：
1.TCP层面的心跳检测，即TCP的Keep-Alive机制，它作用是整个TCP协议栈
2.协议层的心跳检测机制，主要存在于长连接协议中，如MQTT
3.应用层的心跳检测，主要有各业务产品通过约定方式定时给对方发送心跳实现。检测的目的是确认当前链路是否可用，对方是否活着并且能够正常接收和发送消息
netty提提供了心跳检测机制，IdleStateHandler


内存保护
NIO通信的内存保护集中在：
1).链路总数的控制：每条链路都包含接收和发送缓冲区，链路个数太多容易导致内存溢出
2).单个缓冲区的上限控制：防止非法长度或消息过大导致内存溢出
3).缓冲区内存释放：防止因为缓冲区使用不当导致的内存泄露
4).NIO消息发送队列的长度上限控制

1.防止内存池泄露
为提升内存利用率，netty提供了内存池和对象池。需要对内存的申请和释放进行严格的管理，否则容易导致内存泄漏。
对于从内存池申请的对象，使用完毕，一定要及时释放，防止内存泄漏

2.缓冲区溢出保护
对消息解码时，需要创建缓冲区(netty的ByteBuf)。缓冲区的创建方式两种
1)容量预分配，在实际读写过程中若不够再扩展
2)根据协议消息长度创建缓冲区

生产环境中，可能遇到各种畸形码流攻击、协议消息编码异常、消息丢包等问题。要对长度上限控制，有效保护，防止内存溢出。
netty提供的编解码框架，对于解码缓冲区的上限保护很重要，两种方式对缓冲区保护
1)创建ByteBuf时对它的容量上限进行保护性设置(maxCapacity)
2)在消息解码时，对消息长度进行判断，若超过最大容量，抛异常，拒绝分配内存，如LengthFieldBasedFrameDecoder.decode中if(frameLength>maxFrameLength)

3.消息发送队列积压保护
netty的NIO消息发送队列ChannelOutBoundBuffer并没有容量上限，会随着消息的积压自动扩展，直到0X7fffffff
若对方处理速度慢，会导致TCP滑动窗口长时间Wie0，若消息发送方发送速度过快或一次批量发送消息量过大，会导致ChannelOutboundBuffer的内存膨胀，可能会内存溢出
建议配置合适的高水位(writeBufferWaterMark)对消息发送速度进行控制，在发送业务消息时，调用Channel.isWritable方法判断Channel是否可写，若不可写
则不要继续发送，否则会导致发送队列积压，出现OOM

总结
可靠性的设计关键在于对非预期异常场景的保护，应用层协议栈会考虑应用协议异常时通信双方应该怎么正确处理异常，但对于那些不遵循规范实现的客户端，协议规范是无法
强制约束对方，只有可靠性做得足够好，才可以。




netty实践总结
一、netty入门知识准备
1.javaNIO类库
a.缓冲区Buffer
b.通道Channel
c.多路复用器selector
Buffer是一个对象，包含一些要写入或要读出的数据。在NIO库中，所有数据都是用缓冲区处理的。
实质上是一个数组，通常是一个字节数组(ByteBuffer)，也可以是其他。也提供了对数据的结构化访问及维护读写位置(limit)等信息
常用的是ByteBuffer，一个ByteBuffer提供一组功能用于操作byte数组

Channel是一个通道，可以通过它读取和写入数据，网络数据通过Channel读取和写入。通道是双向的，流是单向的。通道可以用于读或写，或同时读写。
Channel是全双工的，可以比流更好的映射底层操作系统的API。特别是UNIX网络编程模型中，底层操作系统的通道都是全双工的，同时支持读和写
常用的是SocketChannel和ServerSocketChannel。

Selector是JavaNIO编程基础，熟练掌握Selector对于掌握NIO编程至关重要
多路复用器提供选择已经就绪的任务的能力。Selector会不断地轮询注册在其上的Channel，若某个Channel上有新的TCP连接接入、读和写事件，这个Channel就
处于就绪状态，会被Selector轮询出来，然后通过SelectionKey获取就绪Channel的集合，进行后续IO处理


2.多线程编程
a.java内存模型
b.关键字synchronized
c.读写锁
d.volatile的正确使用
e.CAS指令和元子类
f.JDK线程池及各种默认实现


二、netty入门学习
通过官网及example进行学习，动手编写和调试代码。
把echo客户端连接创建、服务端连接接入、客户端消息发送和读取、服务端消息发送和读取流程及机制搞清楚，再学习其他协议栈，如HTTP/MQTT等

三、项目实践
只有通过实践才能真正掌握netty，若项目暂时用不到netty，可以学习一些开源的rpc或服务框架，看看这些框架是如何集成并使用netty的，如：
1.grpc，主要用到netty的http/2协议栈
2.vertx，主要用到netty的NIO通信框架和http协议栈，以及EventLoop Reactive线程模型
通过阅读源码+调试学习。很多异步处理，单纯阅读代码很难真正掌握消息的处理流程

四、netty源码阅读策略
根据需要选择性地阅读
首先需要掌握netty的内核工作流程，包括客户端连接创建、客户端消息读写、服务端监听和客户端接入、服务端消息读写，涉及核心类库
1.ByteBuf和各种子类
2.Channel、Unsafe及各种子类
3.ChannelPipeline和ChannelHandler
4.EventLoop和EventLoopGroup及各种子类实现
5.Future和Promise及各种子类实现

netty的客户端和服务端的创建流程需要熟练掌握。
画出流程创建图，结合源码阅读，并进行调试，更高效地掌握知识点



netty故障定位技巧
一、若业务的ChannelHandler接收不到消息，可能原因：
1.业务的解码ChannelHandler存在缺陷，导致消息解码失败，没有投递到后端
2.业务发送的是畸形或错误码流(如长度错误)，导致业务解码ChannelHandler无法正确解码业务消息
3.业务ChannelHandler执行了一些耗时或阻塞操作，导致netty的NioEventLoop被挂住，无法读取消息
4.执行业务ChannelHandler的线程池队列积压，导致新接收的消息排队，没有得到及时处理
5.对方确实没有发送消息

定位策略：
1.在业务的首个ChannelHandler的channelRead中设置断点调试，看是否能读取到消息
2.在ChannelHandler中添加LoggingHandler，打印接口日志
3.查看NioEventLoop线程的状态，看是否发生了阻塞
4.通过tcpdump抓包查看消息是否发送成功

二、内存泄露
jmap -dump:format=b,file=xx pid  打印内存堆栈，使用MemoryAnalyzer对内存占用情况分析，查找内存泄露点，再结合代码进行分析，定位内存泄露具体原因。
Class Name   Shallow Heap   Retained Heap(sorted)  Percentage

三、性能问题
出现性能问题时，先确认是netty的问题还是业务的问题，通过jstack打印线程堆栈，按照线程CPU使用情况排序(top -Hp)，看线程干什么。
通常采集几次发现netty的NIO线程堆栈停留在select操作上，说明I/O比较空闲，性能瓶颈不是netty。
若发现性能瓶颈在网络I/O读写上，可以适当调大NioEventloopGroup中的workI/O线程数，直到I/O处理性能可以满足业务需求





































































































































