package com.netty.demo.future;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @date:2021/2/24 23:26
 **/
public class FutureDemoClientHandler2 extends SimpleChannelInboundHandler<String> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println("客户端222收到服务端的来信---" + msg);
    }

}
