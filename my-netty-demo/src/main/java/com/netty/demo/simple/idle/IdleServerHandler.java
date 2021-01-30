package com.netty.demo.simple.idle;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @description:
 * @date:2021/1/11 14:15
 **/
public class IdleServerHandler extends ChannelInboundHandlerAdapter {

    private static AtomicInteger count = new AtomicInteger(0);

    private static int idleTime = 0;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("客户端第几次建立" + count.incrementAndGet() + " 当前handler的hashCode=" + this.hashCode());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("NettyServerHandler-->thread.currentThread()-->" + Thread.currentThread().getName() + "，时间=" + System.currentTimeMillis());
        System.out.println("服务端接收到客户端的数据是：" + msg.toString());
        ctx.writeAndFlush("你好，客户端，来信已收到");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (IdleState.WRITER_IDLE.equals(event.state())) {
                ctx.writeAndFlush("heartbeat-msg").addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                System.out.println("写超时----");
            } else if (IdleState.READER_IDLE.equals(event.state())) {
                System.out.println("读超时----" + (++idleTime) + "次");
            }
        }
        super.userEventTriggered(ctx, evt);
    }
}
