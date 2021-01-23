package com.netty.demo.simple.debug.demo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * @description:
 * @date:2021/1/23 10:55
 **/
public class DebugServer {

    private static ServerBootstrap server;

    public static void main(String[] args) throws InterruptedException {

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        NioEventLoopGroup boss = new NioEventLoopGroup(10);
        NioEventLoopGroup worker = new NioEventLoopGroup(2);

        serverBootstrap.group(boss, worker)
                .option(ChannelOption.SO_BACKLOG, 16)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new StringEncoder())
                                .addLast(new StringDecoder())
                                .addLast(new DebugServerHandler());
                    }
                });
        server = serverBootstrap;
        ChannelFuture sync = serverBootstrap.bind(8888).sync();
        ChannelFuture sync1 = serverBootstrap.bind(9999).sync();
        ChannelFuture sync2 = serverBootstrap.bind(10000).sync();
        sync.channel().closeFuture().sync();
        sync1.channel().closeFuture().sync();
        sync2.channel().closeFuture().sync();
        boss.shutdownGracefully();
        worker.shutdownGracefully();
    }

}
