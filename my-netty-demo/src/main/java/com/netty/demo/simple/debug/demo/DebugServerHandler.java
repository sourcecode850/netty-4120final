package com.netty.demo.simple.debug.demo;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @description:
 * @date:2021/1/23 11:12
 **/
public class DebugServerHandler extends SimpleChannelInboundHandler<String> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(sdf.format(new Date()));
        System.out.println("客户端" + ctx.channel().remoteAddress() + "说：" + msg);
        ctx.writeAndFlush("我是服务器" + ctx.channel().localAddress());
    }

}
