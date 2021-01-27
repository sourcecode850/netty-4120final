package com.netty.demo.atguigu.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.UUID;

/**
 * @description:
 * @date:2021/1/27 23:03
 **/
public class MyServerHandler extends SimpleChannelInboundHandler<MessageProtocol> {

    private int count;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageProtocol msg) throws Exception {
        // 接收消息，并处理
        int len = msg.getLen();
        byte[] content = msg.getContent();

        System.out.println(String.format("服务端第%s次接收到消息如下,长度=%s，content=%s", ++count, len, new String(content, "utf-8")));

        // 回复消息
        String response = UUID.randomUUID().toString();
        byte[] contentRes = response.getBytes("utf-8");
        int responseLen = contentRes.length;
        //构建协议包
        MessageProtocol messageProtocol = new MessageProtocol();
        messageProtocol.setLen(responseLen);
        messageProtocol.setContent(contentRes);
        ctx.writeAndFlush(messageProtocol);

    }
}
