package com.netty.demo.atguigu.executor;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

/**
 * 在业务handler中直接加入executor，将耗时的业务操作该executor完成
 *
 * @description:
 * @date:2021/1/30 10:04
 **/
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    static final EventExecutorGroup group = new DefaultEventExecutorGroup(1);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // final局部变量
        final Object msgCop = msg;
        final ChannelHandlerContext ctxCop = ctx;
        System.out.println("服务端接收到客户端的数据是：" + msg.toString());
        group.submit(() -> {
            System.out.println("group 处理业务操作----------");
            Thread.sleep(10 * 1000);
            System.err.println(msgCop + "" + Thread.currentThread().getName());
            ctxCop.writeAndFlush("hello i am server ~");
            return null;
        });
        System.out.println("go on ...");
    }

}
