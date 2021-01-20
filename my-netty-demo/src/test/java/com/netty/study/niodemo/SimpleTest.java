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
