
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