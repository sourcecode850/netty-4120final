package com.netty.demo.simple.casa;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @description:
 * @date:2021/1/11 16:05
 **/
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    private static AtomicInteger count = new AtomicInteger(0);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("客户端第几次建立" + count.incrementAndGet() + " 当前handler的hashCode=" + this.hashCode());

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.getBytes(0, bytes);
        System.out.println("自己解码客户端发过来的数据：" + new String(bytes, "utf-8"));
        System.out.println("服务端接收到客户端的数据是：" + msg.toString());
        ctx.writeAndFlush(Unpooled.wrappedBuffer("你好，客户端，来信已收到".getBytes("utf-8")));
    }

}
