package com.netty.demo.mygroupchat.groupchat;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

/**
 * @description:
 * @date:2021/1/11 20:16
 **/
public class GroupChatClient {

    public static void main(String[] args) throws IOException {

        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(new InetSocketAddress("127.0.0.1", 8890));
        if (socketChannel.finishConnect()) {
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNext()) {
                String s = scanner.nextLine();
                System.out.println("-----------");
                socketChannel.write(ByteBuffer.wrap(s.getBytes("utf-8")));
            }
        }

    }

}
