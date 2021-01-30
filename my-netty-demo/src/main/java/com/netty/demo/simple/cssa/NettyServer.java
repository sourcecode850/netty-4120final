package com.netty.demo.simple.cssa;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

/**
 * @description:
 * @date:2021/1/11 14:15
 **/
public class NettyServer {

    static final EventExecutorGroup group1 = new DefaultEventExecutorGroup(1);
    static final EventExecutorGroup group2 = new DefaultEventExecutorGroup(1);
    static final EventExecutorGroup group3 = new DefaultEventExecutorGroup(1);

    public static void main(String[] args) throws InterruptedException {

        ServerBootstrap serverBootstrap = new ServerBootstrap();

        NioEventLoopGroup bossGroup = new NioEventLoopGroup(2);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        serverBootstrap.group(bossGroup, workerGroup)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_BACKLOG, 128)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        System.out.println("pipeline-->thread.currentThread()-->" + Thread.currentThread().getName() + "，时间=" + System.currentTimeMillis());
                        ch.pipeline().addLast(group1, new StringDecoder())
                                .addLast(group2, new StringEncoder())
                                .addLast(group3, new NettyServerHandler());
                    }
                });

        ChannelFuture sync = serverBootstrap.bind(8888).sync();
        sync.channel().closeFuture().sync();
    }

}
