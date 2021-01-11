package com.netty.demo.simple.casa;

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
        System.out.println("服务端接收到客户端的数据是：" + msg.toString());
        ctx.writeAndFlush("你好，客户端，来信已收到");
    }

}
