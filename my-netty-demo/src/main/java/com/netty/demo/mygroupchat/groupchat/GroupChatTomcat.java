package com.netty.demo.mygroupchat.groupchat;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * 模拟tomcat poller机制，解决只有一个channel能触发服务端接收客户端数据的问题
 *
 * @description:
 * @date:2021/1/11 19:59
 **/
public class GroupChatTomcat {

    public static void main(String[] args) throws Exception {

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(true);
        serverSocketChannel.bind(new InetSocketAddress("127.0.0.1", 8890));
        // 启动Poller线程
        Poller poller = new Poller();
        Thread pollerThread = new Thread(poller, "poller-thread-1");
        pollerThread.start();
        while (true) {
            // 这里是阻塞的，将获取的channel交给其他线程处理
            SocketChannel socketChannel = serverSocketChannel.accept();
            System.out.println("客户端建立连接----" + socketChannel.getRemoteAddress());
            poller.socketChannels.put(socketChannel);
        }

    }

    public static class Poller implements Runnable {

        // 队列保存socketChannel
        private Selector selector;

        private final BlockingQueue<SocketChannel> socketChannels =
                new ArrayBlockingQueue<SocketChannel>(1024);

        public Poller() {
            try {
                this.selector = Selector.open();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        @Override
        public void run() {
            // 死循环监听请求
            while (true) {
                try {
                    SocketChannel socketChannel = socketChannels.poll();
                    if (Objects.nonNull(socketChannel)) {
                        socketChannel.configureBlocking(false);
                        SelectionKey key = register(socketChannel);
                        // 分配一个ByteBuffer
                        key.attach(ByteBuffer.allocate(1024));
                    }
                    // 不能有while+selector.select操作，会卡住的；但是不能没有selector.select操作,不然selector.selectedKeys总返回空的集合，
                    // 可以搞个if判断，而不是while循环
                    if (selector.selectNow() > 0) {
                        Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                        while (Objects.nonNull(iterator) && iterator.hasNext()) {
                            SelectionKey selectionKey = iterator.next();
                            // 先删除key，再处理
                            iterator.remove();
                            if (selectionKey.isReadable()) {
                                SocketChannel channel = (SocketChannel) selectionKey.channel();
                                ByteBuffer attachment = (ByteBuffer) selectionKey.attachment();
                                attachment.clear();
                                // 这里读了多少数据，就打印多少数据，否则会出现数据残留的问题
                                int readCount = channel.read(attachment);
                                // 数据的残留问题找到原因了；这里不应该使用attachment.array读取数据
                                System.out.println("客户端" + channel.getRemoteAddress() + "发送的数据=====" + new String(attachment.array(), 0, readCount));
                                // 这里记得将已经处理过的SelectionKey删除掉，否则下次不会再触发了
                                System.out.println("客户端" + channel.getRemoteAddress() + "发送的有残留的数据=====" + new String(attachment.array()));
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }

        public SelectionKey register(SocketChannel socketChannel) throws ClosedChannelException {
            return socketChannel.register(selector, SelectionKey.OP_READ);
        }

    }

}
