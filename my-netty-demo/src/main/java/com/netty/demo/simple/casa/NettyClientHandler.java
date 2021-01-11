package com.netty.demo.simple.casa;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @description:
 * @date:2021/1/11 15:58
 **/
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 连接建立好后，客户端先发送数据给服务端看看
        System.out.println("连接成功，客户端准备发送数据");
        ctx.writeAndFlush(Unpooled.wrappedBuffer("你好，服务器，我是客户端".getBytes("utf-8")));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.getBytes(0, bytes);
        System.out.println("自己解码服务端发过来的数据:" + new String(bytes, "utf-8"));
        System.out.println("客户端收到的数据是：" + msg.toString());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}
