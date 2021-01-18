package com.netty.demo.atguigu.groupchat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

/**
 * @description:
 * @date:2021/1/18 22:55
 **/
public class AtGuiguGroupChatServer {

    private Selector selector;
    private ServerSocketChannel listenChannel;
    private static final int PORT = 6667;

    public AtGuiguGroupChatServer() {
        try {
            selector = Selector.open();
            listenChannel = ServerSocketChannel.open();
            listenChannel.socket().bind(new InetSocketAddress(PORT));
            listenChannel.configureBlocking(false);
            listenChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void listen() {
        try {
            while (true) {
                int count = selector.select();
                if (count > 0) {// 有事件处理
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();

                        if (key.isAcceptable()) {
                            SocketChannel accept = listenChannel.accept();
                            accept.configureBlocking(false);
                            accept.register(selector, SelectionKey.OP_READ);
                            // 提示
                            System.out.println(accept.getRemoteAddress() + "上线");
                        }

                        if (key.isReadable()) {
                            // 处理读
                            readData(key);
                        }

                        iterator.remove();

                    }
                } else {
                    System.out.println("等待...");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //  发生异常处理
        }
    }

    private void readData(SelectionKey key) {
        //  取到管理的channel
        SocketChannel channel = null;
        try {
            channel = (SocketChannel) key.channel();
            // 创建ByteBuffer
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            // 这里要注意，强制关闭客户端，也是一个可读事件！！！！
            int count = channel.read(buffer);
            // 根据count的值进行处理
            if (count > 0) {
                // 把缓存区的数据转化成字符串
                String msg = new String(buffer.array());
                // 输出该消息
                System.out.println("from 客户端:" + msg);
                // 向其他客户端转发消息
                sendInfoToOtherClients(msg, channel);
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            try {
                System.out.println(channel.getRemoteAddress() + "离线了");
                // 取消注册
                key.cancel();
                channel.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    private void sendInfoToOtherClients(String msg, SocketChannel self) throws IOException {
        System.out.println("服务器转发消息...");
        for (SelectionKey key : selector.keys()) {
            // 通过key获取对应的SocketChannel
            SelectableChannel targetChannel = key.channel();

            //排除自己
            if (targetChannel instanceof SocketChannel && targetChannel != self) {
                SocketChannel dest = (SocketChannel) targetChannel;
                // 将msg存储到buffer
                ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());
                // 将buffer的数据写入到通道
                dest.write(buffer);
            }
        }
    }

    public static void main(String[] args) {
        AtGuiguGroupChatServer groupChatServer = new AtGuiguGroupChatServer();
        groupChatServer.listen();
    }

}
