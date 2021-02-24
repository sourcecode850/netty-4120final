package com.netty.demo.future;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * future-listener-debug测试clientHandler
 *
 * @date:2021/2/24 21:41
 **/
public class FutureDemoClientHandler extends SimpleChannelInboundHandler<String> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("FutureDemoClientHandler.channelActive--------" + System.currentTimeMillis());
        ctx.writeAndFlush("你好，服务器，我是客户端");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println("客户端111收到服务端的来信---" + msg);
        // io.netty.handler.codec.ByteToMessageDecoder.channelRead() 末尾会加上这句ctx.fireChannelRead(msg);
        // 表示调用下一个handler进行处理
        ctx.fireChannelRead(msg);
    }

}
