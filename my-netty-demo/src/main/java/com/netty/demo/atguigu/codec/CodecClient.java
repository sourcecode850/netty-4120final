package com.netty.demo.atguigu.codec;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @description:
 * @date:2021/1/27 23:24
 **/
public class CodecClient {


    public static void main(String[] args) throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        NioEventLoopGroup group = new NioEventLoopGroup(1);
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {

                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new MyMessageDecoder())
                                .addLast(new MyMessageEncoder())
                                .addLast(new MyClientHandler());
                    }
                });
        final ChannelFuture sync = bootstrap.connect("127.0.0.1", 7001).sync();
        sync.channel().closeFuture().sync();
    }

}
