## 服务启动

初始化构造channel
ServerBootstrap.bind--doBind--initAndRegister--
channelFactory.newChannel，反射构造NioSocketChannel。AbstractChannel中会构造newChannelPipeline
init

初始化注册
initAndRegister--EventLoopGroup.register--MultithreadEventLoopGroup.register(channel)--
next()就获取了一个EventLoop--EventLoop.register--AbstractChannel.register这里对当前channel绑定eventLoop--
eventLoop.execute中register0会设定回调。

初始化bind
AbstractBootstrap.doBind--doBind0--channel.eventLoop().execute--当有回调时NioServerSocketChannel.bind--
pipeline.bind--tail.bind--findContextOutbound从后向前查找，上下文中也绑定了channel--LoggingHandler.bind--HeadContext.bind--
AbstractChannel.AbstractUnsafe.doBind--NioServerSocketChannel.doBind--javaChannel().bind--ServerSocketChannelImpl.bind--
若绑定成功则eventLoop().execute(pipeline.fireChannelActive)


激活流程：
DefaultChannelPipeline.fireChannelActive--AbstractChannelHandlerContext.invokeChannelActive(head)从头开始--
next.executor且next.invokeChannelActive--HeadContext.channelActive--ctx.fireChannelActive直接传递--findContextInbound
从头向后传递--LoggingHandler.channelActive--ctx.fireChannelActive--TailContext.channelActive


为什么没有经过自己的EchoServerHandler？
因为这会还没有连接进来，也没有init动作
ctx = {DefaultChannelPipeline$HeadContext@1512} "ChannelHandlerContext(DefaultChannelPipeline$HeadContext#0, [id: 0xe1a5be43, L:/0:0:0:0:0:0:0:0:18085])"
next = {DefaultChannelHandlerContext@1523} "ChannelHandlerContext(LoggingHandler#0, [id: 0xe1a5be43, L:/0:0:0:0:0:0:0:0:18085])"
next = {DefaultChannelHandlerContext@1795} "ChannelHandlerContext(ServerBootstrap$ServerBootstrapAcceptor#0, [id: 0xe1a5be43, L:/0:0:0:0:0:0:0:0:18085])"
next = {DefaultChannelPipeline$TailContext@1800} "ChannelHandlerContext(DefaultChannelPipeline$TailContext#0, [id: 0xe1a5be43, L:/0:0:0:0:0:0:0:0:18085])"

ServerBootstrap.handler  --to use for serving the requests. 这个是为全局的
childHandler  --is used to serve the request for the Channel's.这个是针对请求channel的


## 客户端建联
AbstractChannel$AbstractUnsafe.register0--pipeline.invokeHandlerAddedIfNeeded--若firstRegistration则callHandlerAddedForAllHandlers--
pendingHandlerCallbackHead.execute()--callHandlerAdded0--ChannelInitializer.handlerAdded--initChannel--调用到自己实现的ChannelInitializer了--
ch.pipeline.addLast

读取数据
SingleThreadEventExecutor.run--processSelectedKeys--processSelectedKeysOptimized
从k.attachment获取channel，之前注册时就绑定了--unsafe.read--pipeline.fireChannelRead--AbstractChannelHandlerContext.invokeChannelRead(head,msg)
从头开始读....tailContext

写数据
ctx.write--findContextOutbound这从当前ctx又向前了--next.invokeWrite--headContext.write--unsafe.write--outboundBuffer.addMessage--
若是flush则进行刷新了
















