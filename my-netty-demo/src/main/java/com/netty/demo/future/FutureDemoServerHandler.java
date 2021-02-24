package com.netty.demo.future;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * future-listener-debug测试serverHandler
 *
 * @date:2021/2/24 21:41
 **/
public class FutureDemoServerHandler extends SimpleChannelInboundHandler<String> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("FutureDemoServerHandler.channelActive-------" + System.currentTimeMillis());
        ctx.writeAndFlush("服务端active啦");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println("服务端收到客户端的来信---" + msg);
        ctx.writeAndFlush("你好，客户端，我是服务器");
    }


}
