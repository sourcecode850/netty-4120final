package com.netty.demo.simple.casa;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * @description:
 * @date:2021/1/11 16:01
 **/
public class NettyServerCasa {

    public static void main(String[] args) throws InterruptedException {

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(new NioEventLoopGroup(), new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        // ch.pipeline().addLast(new StringDecoder());
                        // ch.pipeline().addLast(new StringEncoder());
                        ch.pipeline().addLast(new NettyServerHandler());
                    }
                });

        ChannelFuture sync = bootstrap.bind("127.0.0.1", 8889).sync();
        sync.channel().closeFuture().sync();

    }

}
