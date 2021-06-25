netty is a NIO client-server framework, which enables quick and easy development of network application.

all problems in computer science can be solved by another level of indeirection

NIO -> non-blocking io

a channel represents a connection to an entity capable of performing IO operations such as a file or a socket

a selector is a NIO component that determines if one or more channels are ready for reading and/or writing

netty addresses this problem by providing a unified API, which allows the same semantics to work seamlessly on either java 6 or 7.
benefit from a simple and consistent API
解决了部分scatter和gatter的oom问题，以及epol的bug问题



netty uses channel handlers to allow a greater separation of concerns, making it easy to add, update, or remove business logic as they evolve

a bootstrap is a construct netty provides that makes it easy for you to configure how netty should setup or bootstrap the application

in order to allow mulltiple protocols and various ways of processing data, netty has what are called handlers.
handlers, are designed to handle a specific event or sets of events in netty

the role of teh channelInitializer is to add ChannelHandler implementations to what's called the ChannelPipeline.
an ChannelInitializer is also itself a ChannelHandler which automatically removes iteself from the ChannelPipeline after it has added the other handlers

an EventLoops purpose in the application is to process IO operations for a Channel.
a single EventLoop will typically handle events for multiple Channels.
The EventLoopGroup itself may contain more than one EventLoop and can be used to obtain an EventLoop

a Channel is a representation of a socket connection or some component capable of performing IO operations, hence why it is manged by the EventLoop
whose job it is to process IO

All IO operations in netty are performed asynchronously.
netty uses Futures and ChannelFutures. can be used to register a listener, which will be notified when an operation has either failed or
completed successfully

a ChannelFuture is a placeholder for a result of an operation that is executed in the future. will be executed and all operations that
return a ChannelFuture and belong to the same Channel will be executed in the correct order, which in the same order as you executed the methods

netty is a non-blocking, event driven, networking framework.
netty uses threads to process IO events.

Think of EventLoops as the threads that perform the actual work for a channel
The EventLoop is always bound to a single Thread that never changed during it's life time
when a channel is registed, netty binds that channel to a single EventLoop for the lifetime of that channel.

client bootstraps/applications use a single EventLoopGroup while ServerBootstrap users 2.
a ServerBootstrap can be thought to have two sets of channels. The first set containing a single ServerChannel representing the server's own socket
which has been bound to a local port. The second set containing all the Channel representing the connections, which teh server has accepted.
By having two EventGroups, all connections can be accepted, evnet under extremely high load beacause the EventLoops accepting connections are noe
shared with those processing teh already accepted connections

The EventLoopGroup may contain more than one EventLoop.
Each Channel will hava one EvnetLoop bind to once it was created which will never chagne.
many channels will share the same EventLoop.you must not block the EventLoop in all case

handlers depend upone the ChannelPipeline to prescribe their order of execution.

data is said to be outbound if the expected flow is from the user application to the remote peer.
data is inbound if it is coming from the remote peer to the user application.
read: socket -> head -> inboundhandler -> tail -> userapp
write: socket <- head <- outboundhandler <- tail <- userapp

inbound and outbound  handlers have a diffrent interface that bot extend ChannelHandler. netty can skip any handler that is not of a
particular type and hence not able to handle a give operaiont.

once a ChannelHandler is added to a Channelpipeline it also gets what's called a ChannelHandlerContext.
user it to write/send message.
writing to teh channel directly causes the message to start from teh tail where as writing to the context object causes the message to
start from the next handler in the ChannelPipeline.

netty provides a series of `adapter` classes which makes things a bit easier.

this conversion will always happen when sending data over the network, byte-message or message-bytes, because you can only transfer bytes
across a network

your application should try not to block the IO thread as this could lead to performance issues in some throughout environments



the most important tasks of a network application is transferring data. bytes over the wire.
Transports help abstract how the data is transferred.  transport更加抽象更加易用，对于传输数据

netty offers a unified API on top of its tarnsport implementation, which makes this situation easier.


using netty from blocking io to nio, netty exposes the same API for every transport implementation, it doesn't matter what
implementation you use.
netty exposes its operations through the Channel interface and its ChannelPipeline and ChannelHandler

at the heart of the transport API is the channel interface, which is used for all of the outbound operations.
a channel has a ChannelPipeline and a ChannelConfig assigned to it
the ChannelConfig has the entire configuration settings stored for the channel and allows for updating them on the fly.
the ChannelPipeline holds all of the ChannelHandler instances that should be used for the inbound and outbound data that is passed through the channel

The ChannelPipeline implements the Intercepting Filter Pattern, which means you can chain different ChannelHandlers and intercept the data
or events which go through the ChannelPipeline
can also modify the ChannelPipeline on the fly, which allows you to add/remove ChannelHandler instances whenever needed.

isActive() returns if the channel is active, which means it's connectd to the remote peer

you'll always operate on the same interfaces, which gives you a high degree of flexibility and guards you from big refactoring once you
want to try out a different transport implementation.

the NIO transport provides a full asynchronous implementation of all I/O operations by using the selector-based approach

a users can register to get notified once a channel's state changes.
the possible changes are:
a new Channel was accepted and is ready.
a Channel connection was completed.
a Channel has data received that is ready to be read.
a Channel is able to send more data on the channel.

selection operation bit-set
OP_ACCEPT  get notified once a new connection is accepted and a channel is created
OP_CONNECT  get notified once a connection attempt finishes.
OP_READ  get notified once data is ready to be read out of the channel
OP_WRITE   get notified once it's possible to write more data to the channel. may be because the OS socket buffer is completely filled.
usually happends when you write faster then the remote peer can handle it


NIO transport offers zero-file-copy, allows you to quickly and efficiently transfer content from your file system. provides a way to transfer
the bytes from the file system to the network stack without copying the bytes from the kernel space to the user space.
only be able to benefit if you don't use any encryption/compression of the data. Otherwise it will need to copy the bytes first to the user
space to do the actural work, so only transferring the raw content of a file makes use of this future. for instance, FTP or HTTP server big files.

local transport can be used to communicate within a VM and still use the same API your're used to. is fully asynchronous as NIO

the embedded transport allows you to interact with your different ChannelHandler implementation more easily. used in test cases to test a
specific ChannelHandler implementaion, can also be used to re-use some ChannelHandler in your own ChannelHandler without event extend it.

Optimal transport for an application
Low concurrent connection count  OIO  |unser 1,000 concurrent connections/ your connections are very active the context switching that OIO needs might be a big impact
High concurrent connection count  NIO  |it's possible to serve between tens of thousands and hunreds of thousands of concurrent connections.
Low latency  OIO  |it's a tradeoff, trade lower latency with higher thread count
Blocking code base  OIO
Communication within the same JVM  Local
Testing your ChannelHandler implementations  Embedded



whenever you need to transmit data it must involve a buffer. the ByteBuf's purpose is to pass data through the netty pipeline.
it was designed from the groud up to address problems with the JDK's ByteBuffer and to meet the daily needs of networking application developers,
making them more productive.

netty uses reference-counting to knowing when it's safe to release a Buf and its claimed resources.
ByteBuf--The byte data container, efficient, convenient, easy-to-use datastructure.

heap Buffers
stores its data in the heap space of the JVM. is done by storing it in a backing array.

Direct Buffer
it allocates the memory directly, which is outside the heap.
must take this into account when calculating the maximum amount of memory your application will use and how to limit it, as the max heap size won't be enough.
in fact, if you use a nondirect buffer, the jvm will make a copy of your buffer to a direct buffer internally before sending it over the socket.
非直接内存在发送时要进行拷贝
they'are more expensive to allocate and de-allocate, this is whey netty supports pooling, which makes this problem disappear.

CompositeByteBuf
allows you to compose different ByteBuf instances and provides a view over them.

discardable bytes|readable bytes|writable bytes|
0              readerIndex     writerIndex     capacity

discardReadBytes()会将已读的丢掉，然后将readIndex和writeIndex向左移动
be aware that this will most likely involve a memory copy as it needs to move the readable bytes(content) to the start fo the ByteBuf.
isn't free and may affect performance, so only use it if you need it and will benefit from it.

you learned about the data containers that are used inside of netty.



netty provides you with a powerful approach to archive processing of incomming data and outgoing data. it allows the user to hook
in ChannelHandler implementations that process the data. can chain them so each ChannelHandler implementation can fullfill small tasks.

A ChannelPipeline is a list of ChannelHandler instances that handle or intercept inbound and outbound operations of a channel.
for each new channel, a new ChannelPipeline is created and attached to the channel. the channel cannot attach another ChannelPipeline to it
or detach the current ChannelPipeline from it.

inbound I/O evnet is triggered it's passed from the beginning to the end. for outboudn I/O begins at the end to start.

each ChannelHandler that is added to the ChannelPipeline will process the event that is passed through it in the IO-Thread, which means you
must not block as otherwise you block the IO-Thread and so affect the overall handing of I/O.

ChannelPipeline extends ChannelInboundInvoker and ChannelOutboundInvoker, it exposes additional methods for invoking inbound and outbound operations.

write()  Requests to write the given message to the Channel, will not write the message to the underlying Soket, but only queue it.
for have it written to the actural Socket yet need to call flush()

Each time a ChannelHandler is added to a ChannelPipeline, a new ChannelHandlerContext is created and assigned.
The ChannelHandlerContext allows the ChannelHandler ot interact with other ChannelHandler implementations and at the end with the underlying
transport, which are part of the same ChannelPipeline.

the differenet is that if you call them on the Channel or ChannelPipeline they always flow through the complete Channelpipeline. in contrast,
if you call a method on the ChannelHandlerContext, it starts at the current position and notify the closes ChannelHandler in the ChannelPipeline.
前者经过整个pipeline，后者从当前开始传播

ChannelHandlerContext是线程安全

the lifecycle states of a channel
state  Description
channelUnregistered  the channel was created, but it isn't registered to its EventLoop
channelRegistered  the channel is registered to its EventLoop
channelActive  the channel is active, which means it's connected to its remote peer, now possible to receive and send data
channelInacttive  the channel isn't connected to the remote peer

the parent of all of them is the ChannelHandler. provides lifecycle operations.

ChannelInboundHandler handles received messages and so the @Override the channelRead method is responsible to release resources.

netty users reference counting to handle pooled ByteBuf's. it is important to make sure the reference count is adjusted after a ByteBUf is completly processed.
ChannelInboundHandlerAdapter does not call "release" on the message once it was pased to channelRead and so its the responsesibility
of the user to do so if the message was consumed.

ChannelInitializer allows you to init the Channel once it's registered with its EventLoop and ready to process I/O
is mainly used to set up the ChannelPipeline for each Channel that is created.



the decoder is for inbound and the encoder is for outbound data.
A decoder is responsible for decoding inbound data from one format to another one.

an encoder is responsible for encoding outbound data from one foramt to another.

using a codec will force you to have both the decoder and encoder in the Channelpipeline or none of these

ByteToByteCodec/ByteToMessageCodec/MessageToMessageCodec

4 3 2 1
the channel pipeline is like a LIFO queue for inbound messages and a FIFO queue for outbound messages.

SPDY(SPeeDY) is one of the new movements when it comes to HTTP. can find some of its 'ideas' in the upcoming HTTP 2.0
the idea of SPDY is to make transfer of content much faster. this is done by :
GZIPing everything
Encrypting everything
Allowing multiple transfers per connection
Providing support for different transfer priorities



netty provides you with an easy and unified way to bootstrap your servers and clients.
the purpose of it is to simplify the process of combining all of the components(channels, pipeline, handlers, and so on).

one is used for server-like channels that accept connections and create "child" channnels for the accepted connections.
the second is for "client-like channels" that don't accept new connections and procss everything in the "parent" channel.

for UDP, don' necessary to have a channel per connection, as is the case with TCP connections. a single channel can process all the data
and no parent-child channel relationship is required.

before we go into much detial let's look at the various methods it provides. table 9.1 gives an overview of them.

methods to bootstrap  --针对client场景
Name  Description
group()  Set the EventLoopGroup, which should be used by the bootstrap. This EventLoopGroup is used to serve the I/O of the Channel.为了IO
channel()  The class of Channel to instance. if the channel cann't be created via a no-args constructor, you can pass in a ChannelFactory for this purpose.
要是不能用无参构造，可以传入工厂
localAddress()  The local address the Channel should be bound to. If not specified, a randdom one will used by the operating system. Alternatively, you
can specify the localAddress on the bind() or connect()  --作为客户端，一般是随机
option()  ChannelOptions to apply on the Channel's ChannelConfig. Those options will be set on the channel on the bind or connect method depending on
what is called first. Changing them after calling those methods has no effect. Which ChannelOptions are suported depends on the actual channel you'll use.
调用方法前设定有效。
attr()  Allow applying attributes on the channel. Those opitons will be set on the channel on the bind or connect method, depending on what is called first.
Changing them after calling those method has no effect.设定channeld的属性,也是调用方法前设定有效。
handler()  Set the ChannelHandler that's added to the ChannelPipeline of the channel and so receive notification for events.
clone()  Clone the Bootstrap to allow it to connect to a different remote peer with the same settings as on the original Bottstrap.
remtoeAddress()  Set the remote address to connected to. Alternatively you can also specify it when calling connect()
connect()  Connect to the remote peer and return a ChannelFuture, which is notified once the connection operation is complete. This can either be
because it was successfull or because of an error. Be aware that this method will also bind the Channel before. 连接前进行绑定
bind()  Bind the channel and return a ChannelFuture, which is notified once the bidn operation is complete. This can either be because it was successful
or because of anerror. Be aware that you will need to call Channel.connect() to fianlly connect to the remote peer after the bind operation success.
先进性绑定然后连接

The bootstrap is responsible for client and/or connectionless-based channels, so it will create the channel after bind() or connect is called.
绑定或连接后会创建channel
调用connect则底层会绑定并创建channel后进行连接



the EventLoop that is assigned to the Channel is responsible to handle all the operations for the Channel. you execute a method that returns a
ChannelFuture it will be executed in the EventLoop that is assigned to the Channel.
The EventLoop is executed by the Thread that is bound to it.
The EventLoopGroup contains a number of EventLoops and is responsible to assign an EventLoop to the Channel during it's registration.
channle注册时会被分配一个EventLoop


Method of ServerBootstrap
Name  Description
group()  Set the EventLoopGroup, which should be used by the ServerBootstrp. This EventLoopGroup is used to serve the I/O of the ServerChannel
and accepted Channels.
channel()  The class of the ServerChannel to instance. If the channel can't be created via a no-args constructor, you can pass in a ChannelFactory.
localAddress()  The local address the ServerChannel should be bound to. If not specified, a random one will be used by the operating system.
option()  ChannelOptions to apply on the ServerChannel ChannelConfig. Those options will be set on the channel on bind or connect method depending
on what's called first. Changing them after calling those methods has no effects.这个是针对ServerSocketChannel的配置，即acceptChannle的。
childOption()  ChannelOptions to apply on the accepted Channels ChannelConfig.用于接收连接的chnannel。 Those options will be set on the channels
once accepted.
attr()  Allow applying attributes on the ServerChannel. Those attributes will be set on the channel on the bind().绑定后不能修改
childAttr()  Allow applying attributes on the accepted Channels. Those attributes will be set on the Channel once accepted.接收后再设定无效
handler()  Set the ChannelHandler that is added to the ChannelPipeline of the ServerChannel and receive notification for events.
childHandler()  Set the ChannelHandler that's added to the ChannelPipeline of the accepted Channels and receive notification for events.用于接收到的channle
The difference is between handler() and childHandler() is that handler() aloows to add a handler which is processed by the "accepting" ServerChannel,
which childHandler() aloows to add a handler which processed by the "accepted" Channel. The accepted Channel represents here a bound Socket to a remote peer.
一个是接收channel，一个是建联后的channel
clone()  Clone the ServerBootstrap and allow using it to connect to a different remote peer with the same settings as on the original ServerBootstrap.
bind()  Bind the ServerChannel return to a ChannelFuture, which is notified once the connection operation is complete.可成功或失败。

with the child prefix method, are responsible for creating child channels. Those channels represent the accepted connections.

Bootstrap will crate a new channel when calling bind().  This channel will then accept child channels once the bind is successful.
Accept new connections and create child channels that will serve an accepted connection.

the child* methdos will operate on the child Channels, which are managed by the ServerChannel.

One of netty's strengths is its ability to "stack" many ChannelHandlers in the ChannelPipeline and write reusable code.

netty allows you to apply ChannelOptions to a bootstrap, are automatically applied to all channels create in the bootstrap.

attribute allow you to associate data with channels in a safe way.



netty offers an easy way to "stack" different ChannelHandler implementations together and so build up its ChannelPipelien.
this decision allows you to separate the logic into small reusable implementations, which only handle one task each.
this not only makes the code cleaner but also allow to easier testing.

for testing ChannelHandler your best bet is to use EmbeddedChannel

we could just rely on the belief that it is working, but this is not what you want. a unit-test is teh only way to know everything works.

it is as easy as add the SslHandler to the ChannelPipeline.



SPDY was developed by Google to solve their scaling issues. One of its main tasks is to make the "loading" of content as fast as possible.

For this SPDY does a few things:
Every header that is transferred via SDPY gets compressed via GZIP. Compression the body is optional as it can be problematic for Proxy servers.
Everything is encypted via TLS
MUltiple transfer per connection possible
Allow you to set priority for different data enabling critical files to be transferred first

SPDY uses a TLS-Extension, which is called Next Protocol Negotiation(NPN) to choose the protocol.

jetty NPN Library is an external library, is responsible for the Next Protocol Negotiation, which is used to detect if the client supports SPDY or not

you should always provide the ability to fallback to HTTP as otherwise you will give many users a bad experience while using your service.


Responsibilities of the ChannelHandlers in SPDY
Name  Responsibility
SslHandler  Encrypt/Decrypt data which is exchanged between the two peers
SpdyFrameDecoder  Decode the received bytes to SPDY frames
SpdyFrameEncoder  Encoder SPDY frmaes back to bytes
SpdySessionHandler  SPDY session handling
SpdyHttpEncoder  Encoder HTTP messages to SPDY frames
SpdyHttpDecoder  Decoder SPDY frames into HTTP messages
SpdyHttpResponseStreamIdHandler  Handle the mapping between requests and responses based on the SPDY id
SpdyRequestHandler  Handle the FullHttpRequests, which were decoded from SPDY frame and so allow trasnparent usage of SPDY.


Responsibilities of the ChannelHandlers in HTTP(s)
SslHandler  Encrypt/Decrypt data which is exchanged between the two peers
HttpRequestDecoer Decode the received bytes to Http requests
HttpResponseEncoder  Encode Http responses to bytes
HttpObjectAggregator  aggregates an HttpMessage and its following HttpContents into a single FullHttpRequest or FullHttpResponse
HttpRequestHandler  Handler the FullHttpRequests, which were decoded



UDP is a connection-less protocol, which is normally used if performance is most critical and loosing packets is not an issue.
one of the most well known UDP based protocols is DNS

thanks to its unified Transport API, netty makes wrting UDP based applications easy as taking a breath.
this allows you to reuse existing ChannelHandlers and other utilities that you have written for other Netty based Applications.

as stated before UDP is connection-less. which means that there is not "connection" for the lifetime of teh communication between clients and servers.
UDP has no "error correction" like TCP. is more like "fire and forget", which means it has no idea if the data sent was received at all.

those "limitations" are one of the reasons why UDP is so fast compared to TCP.
as it eliminates all the overhead of handshaking and other overhead that comes with TCP.

UDP is only a good fit for applications that can "handle" the case of lost messages. which means it is not a very good fit for applications
that can't lose any message, like an application that handles money transactions.

UDP is a perfect fit for such an application as it may not be to dramatic if you loose one log line as it is sotred on the base-system anyway.



easily implement custom codecs with netty for your favorite protocol. those codecs are easy to reuse and test, thanks to the flexible
architecture of netty

whenever you are about to implement a codec for a give protocol, you should spend some time to understantd its workings.

in general the protocol has a 24 bytes header for requests and responses.
Sample Memocached header byte structure
Field  Byte offset  Value
Magic  0  0x80 for request 0x81 for responses
OpCode 1  0x01..0x1A
Key length  2 and 3  1..32,767
Extra length  4  0x00, x04 or 0x08
Data type  5  0x00
Reserved  6 and 7  0x00
Total body length  8-11  Total size of body inclusive extras
Opaque  12-15  Any signed 32 bi integer, also included in the response and so make it easier to map requests to responses.
CAS  16-23  Data version check

the data send over the network is always a series of bytes

encoder handls outbound and decoder inbound.
encoder will encode data which is about to get written to the remote peer. the decoder will handle data which was read from the remote peer.

in a real world implementation you would most likely want to place in some verification checks(max size..) and raise an EncoderException
or DecoderException if there is protocol violation was detected

without tests you will only see f it works when running it against some real server, which is not what you should deppend on.

here are some thinsg you should generally take care of:
Testing with fragmented and non fragmented data
Test validation if received data/send data if needed



the thread model defines how the application or framework executes your code, so it's important to choose the right thread model for application.

all of your ChannelHandlers, which contain your busines logic, are guaranteed to be executed by a single thread at the same time for a specific Channel.

it does limit each connection to one thread as this design works well for nonblocking execution.

creating threads and recycling them comes with overhead.

using multiple threads comes with the cost of managing resources and, as a side effect, introduces too much context switching.

netty4
the event loop runs events in a loop until it's terminated.
this fits well in the design of network frameworks as they need to run events for a specific connection in a loop when these occur.

if the executing thread is bound to the EventLoop the operation will get executed directly. If not, the thread will be queued and executed once
the EventLoop is ready.

after the event is passed to the EventLoop, a check is performed to detect if the calling thread is the same as the one assigned to the EventLoop

the important thing is that the logic used is generic and flexible enough to handle all kinds of use cases.

netty3
netty guaranteed only that inbound events were executed in the I/O thread. all outbound events were handled by calling the thread.
put the burden on you to synchronize your ChannelHandlers that handle these events, as it wasn't guaranteed that only one thread operated on
them at the same time.


netty's thread model internals
the EventLoop is responsible for handling all events for a Channel during its lifetime.
if the thread is the same as the one of the EventLoop, the code block in question is executed. If the thread is different, it schedules a
task and puts it in an internal queue for later execution.

it's important to ensure that you never put any long-running tasks in the execution queue, because once the task is executed and run it will
effectively block any ohter task from executing on the same thread.

never block the I/O thread

scheduling implementation internals
the actual implementation in netty is based on the paper "Hashed and hierachical timing wheels: Data structures to efficiently implemetn timer facility"
the scheduled execution may be not 100% acurate.

if you don't follow netty\s thread model protocol, you will need to synchronize the concurrent access on your own.

netty uses a thread pool to hold threads that will serve the I/O and events for a Channel.

how the thread pool is allocated:
1.all threads get allocated out of this thread pool. here it will use three threads
2.thread allocated out of the thread pool. this thread handles all events and tasks for all the channels assigned to it, via the EventLoop
3.Channels that are bound to the thread and so call all operations are always executed by teh same thread during the life-time of the Channel.
A channel belongs to a conneciton.

these three threads will get assigned to each newly created channel, which is created per conneciton. this is done through the EventLoopGroup
implementation, uses the thread pool under the hood to manage the resources.

Once a Channel is assigned to a thread it will use this thread throughout its lifetime.
what won't change is the fact that only one thread will operate on teh I/O of a specific channel at the same time.

netty线程模型：
每个连接，对应一个channel，被分配到一个EventLoop上，对应一个线程，
这样，执行读写时，这个channel对应的handler执行，都会到一个线程上，
每个evenLoop会执行多个channel，但是每个channell的执行都是单线程的。



each Channel needs to be registered on an EventLoop to process its IO / Events.
this is done for you during the bootstrap process automatically
