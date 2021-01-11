
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