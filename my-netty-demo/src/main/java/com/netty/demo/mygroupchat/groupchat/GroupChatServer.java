package com.netty.demo.mygroupchat.groupchat;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * @description:
 * @date:2021/1/11 19:59
 **/
public class GroupChatServer {

    public static void main(String[] args) throws IOException {

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(true);
        serverSocketChannel.bind(new InetSocketAddress("127.0.0.1", 8890));

        final Selector selector = Selector.open();

        boolean notStarted = true;

        while (true) {
            // 这里是阻塞的，将获取的channel交给其他线程处理
            SocketChannel socketChannel = serverSocketChannel.accept();
            System.out.println("客户端建立连接----" + socketChannel.getRemoteAddress());
            socketChannel.configureBlocking(false);
            SelectionKey key = socketChannel.register(selector, SelectionKey.OP_READ);
            // 分配一个ByteBuffer
            key.attach(ByteBuffer.allocate(1024));
            if (notStarted) {
                notStarted = false;
                new Thread(() -> {
                    try {
                        while (selector.select() > 0) {
                            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                            while (iterator.hasNext()) {
                                SelectionKey selectionKey = iterator.next();
                                if (selectionKey.isReadable()) {
                                    SocketChannel channel = (SocketChannel) selectionKey.channel();
                                    ByteBuffer attachment = (ByteBuffer) selectionKey.attachment();
                                    attachment.clear();
                                    channel.read(attachment);
                                    System.out.println("客户端" + channel.getRemoteAddress() + "发送的数据=====" + new String(attachment.array()));
                                    // 这里记得将已经处理过的SelectionKey删除掉，否则下次不会再触发了
                                    iterator.remove();
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }).start();
            }
        }

    }

}
