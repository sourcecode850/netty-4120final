
简单的server-client通信

(1) 基于 客户端处理器-SimpleChannelInboundHandler，服务端处理器-ChannelInBoundHandlerAdapter；即cssa
客户端和服务器需要加解码器和编码器，否则handler不起作用的;

如果客户端不加解码器，服务器发送过来的消息，客户端下面的方法判断进无法进入，客户端处理期就没法处理数据；
io.netty.channel.SimpleChannelInboundHandler.acceptInboundMessage(msg)
客户端不加解码器，msg的类型是 PooledUnSafeDirectByteBuf；因为没有解码器，客户端收到的信息就是最原始的
ByteBuf，无法匹配到客户端处理器需要的string类型数据，所以客户端看不到服务器的返回

io.netty.util.internal.TypeParameterMatcher.ReflectiveMatcher.match
@Override
public boolean match(Object msg) {
    return type.isInstance(msg);
}

客户端加了解码器后，acceptInboundMessage(msg)方法就已经解码成字符串了，这个时候客户端处理器就能处理了

(2) 基于 casa的方式，如果不加解码器和编码器，客户端和服务端互传的消息还是无法被业务handler处理

2.1 服务端客户端都不加解码器和编码器
serverHandler和clientHandler都无法处理数据，channelRead方法没被调用

2.2 服务端和客户端都加StringEncoder和StringDecoder
可以正常处理数据

2.3 服务端加StringEncoder和StringDecoder，客户端加StringEncoder
serverHandler可以正常处理数据，clientHandler不能正常处理数据，但是channelRead方法能被调用
客户端收到的数据是：PooledUnsafeDirectByteBuf(ridx: 0, widx: 36, cap: 1024)

2.4 服务端加StringEncoder和StringDecoder，客户端都不加
serverHandler和clientHandler都无法处理数据，channelRead方法没被调用

debug发现-io.netty.channel.AbstractChannel.AbstractUnsafe.write
如果客户端不加编码器，最后发送数据的时候，会报错误的
java.lang.UnsupportedOperationException: unsupported message type: String (expected: ByteBuf, FileRegion)
而且这异常不会被clientHandler的exceptionCaught方法捕获到

io.netty.channel.nio.AbstractNioByteChannel.filterOutboundMessage
@Override
protected final Object filterOutboundMessage(Object msg) {
    if (msg instanceof ByteBuf) {
        ByteBuf buf = (ByteBuf) msg;
        if (buf.isDirect()) {
            return msg;
        }

        return newDirectBuffer(buf);
    }

    if (msg instanceof FileRegion) {
        return msg;
    }

    throw new UnsupportedOperationException(
            "unsupported message type: " + StringUtil.simpleClassName(msg) + EXPECTED_TYPES);
}

总结：如果没有编码器不能发数据，除非发送的数据业务自己构造成ByteBuf类型，但是没有解码器还是可以收数据的，不过收到的数据是ByteBuf类型的

几种channel的继承关系
NioSocketChannle -> AbstractNioByteChannel -> AbstractNioChannel -> AbstractChannel -> DefaultAttributeMap -> Object

(3)不使用StringEncoder和StringDecoder，发送数据的时候封装成ByteBuf，接收数据将ByteBuf转化成需要的类型即可

(4) debug，看看netty处理时序是怎么样的；
客户端继承 ChannelInboundHandlerAdapter: 可以看到是先注册，再激活，读取数据，读取完毕后再执行 channelReadComplete
    channelAdded=========
    channelRegistered==========
    channelActive==========
    收到服务端的消息=我是服务器/127.0.0.1:8888
    channelReadComplete==========

4.1 启动客户端的时候，创建SingleThreadEventExecutor，里面包含线程对象thread；但是启动的时候，thread是null，而CurrentThread
是启动客户端的main线程，在main线程中会启动：io.netty.util.concurrent.SingleThreadEventExecutor.doStartThread
创建SingleThreadEventExecutor的时候，会创建ThreadPerTaskExecutor，这个线程负责完成thread的创建和启动：
thread的类型是FastThreadLocalThread = Thread[nioEventLoopGroup-2-1,10,main]

客户端创建多个NioEventLoop只有第一个NioEventLoop对象的thread成员有值的；这个thread继承自：SingleThreadEventExecutor
io.netty.util.concurrent.SingleThreadEventExecutor.ST_NOT_STARTED
io.netty.util.concurrent.SingleThreadEventExecutor.ST_STARTED



客户端handler处理分析：

channelAdded
channelRegistered
channelActive
 好像都是runAllTasks执行的

 1. runAllTasks是在什么线程执行的？
    nioEventLoopGroup，自定义和schedule任务都是的；但是要注意，nioEventLoop线程一般不止一个，而是一个组；所以debug时候有时候看到
    scheduledTaskQueue明明初始化了，看到的还是null；因为这是在不同的线程中看到的，serverHandler提交scheduledTask的时候，
    只会有一个NioEventLoop完成，因此只有这个线程才有scheduledTask。为了方便debug，建议都使用单线程，除非是研究多线程情况；
    就算boss，work都搞一个线程，也老是debug到boss线程，而scheduledTask是注册到worker线程的；如何debug到worker线程是关键；换个思路，
    Server端总是有boss线程的，而client端只有worker线程，所以把任务交给client发就好了；

 2. runAllTasks中到底执行了哪些task？ taskQueue和scheduledTaskQueue
 3. scheduledTaskQueue使用和原理，其实就是简单的优先级队列，自己改拔改拔变成scheduled，看起来有scheduled的味道



