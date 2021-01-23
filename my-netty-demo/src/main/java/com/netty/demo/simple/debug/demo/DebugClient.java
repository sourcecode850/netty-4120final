package com.netty.demo.simple.debug.demo;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * @description:
 * @date:2021/1/23 10:55
 **/
public class DebugClient {

    public static void main(String[] args) throws InterruptedException {

        Bootstrap bootstrap = new Bootstrap();
        NioEventLoopGroup worker = new NioEventLoopGroup(2);
        bootstrap.group(worker)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new StringDecoder())
                                .addLast(new StringEncoder())
                                .addLast(new DebugClientHandler());
                    }

                });

        ChannelFuture sync = bootstrap.connect("127.0.0.1", 8888).sync();
        sync.channel().closeFuture().sync();
        worker.shutdownGracefully();
    }

}
