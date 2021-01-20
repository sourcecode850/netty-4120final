package com.netty.study.niodemo;

import org.junit.Test;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * @description:
 * @date:2021/1/20 21:21
 **/
public class SimpleTest {

    /**
     * 一开始文件是空的，执行下面的代码后，"HELLO"写入到了文件中，但是还写了5个空格
     * <p>
     * mmap与sendfile的区别：传统的IO模型需要两次拷贝，mmap还是需要一次拷贝的，虽然用户程序和内核共享了内核缓冲区，
     * 不需要将内核缓冲区的数据拷贝到用户缓冲区，但是仍然需要CPU将内核缓冲区中的数据拷贝到socket buffer中，所以mmap减少了一次拷贝
     * 而sendfile 数据不经过用户态，减少了用户态和内核态的上下文切换；sendfile中，cpu直接将内核数据拷到socket buffer
     * linux2.4继续优化，不再将数据拷贝到socket buffer，而是将数据直接拷贝到protocol engine；这里其实也有一次拷贝，就是将
     * 文件的length和offset拷贝交给DMA处理，DMA拿到这些信息，将kernel buffer数据直接拷贝到协议栈
     * <p>
     * hard drive -> kernel buffer 需要DMA
     * socket buffer -> protocol engine 仍然需要DMA的
     *
     * mmap 必须将内核数据拷贝到socket buffer，而sendfile可以交给DMA处理
     * mmap适合小文件读写，而sendfile适合大文件传输
     *
     * @throws IOException
     */
    @Test
    public void mappedByteBufferTest() throws IOException {
        FileChannel channel = FileChannel.open(Paths.get("G:\\新建文件夹\\nio\\mappedByteBufferTest.txt"), StandardOpenOption.READ, StandardOpenOption.WRITE);
        MappedByteBuffer mappedByteBuffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, 10);
        mappedByteBuffer.put(0, (byte) 'H');
        mappedByteBuffer.put(1, (byte) 'e');
        mappedByteBuffer.put(2, (byte) 'l');
        mappedByteBuffer.put(3, (byte) 'l');
        mappedByteBuffer.put(4, (byte) 'o');
        mappedByteBuffer.force();
        System.out.println(channel.size());
    }


}
