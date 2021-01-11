package com.netty.demo.simple.cssa;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @description:
 * @date:2021/1/11 14:15
 **/
public class NettyClientHandler extends SimpleChannelInboundHandler<String> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 连接建立好后，客户端先发送数据给服务端看看
        System.out.println("连接成功，客户端准备发送数据");
        ctx.writeAndFlush("你好，服务器，我是客户端");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println("客户端收到服务端发过来的数据是 = " + msg);
    }

}
