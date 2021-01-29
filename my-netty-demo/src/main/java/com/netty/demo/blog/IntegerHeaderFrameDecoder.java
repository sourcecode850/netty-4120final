package com.netty.demo.blog;

import com.netty.demo.blog.IntegerHeaderFrameDecoder.MyDecoderState;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

import static com.netty.demo.blog.IntegerHeaderFrameDecoder.MyDecoderState.READ_CONTENT;
import static com.netty.demo.blog.IntegerHeaderFrameDecoder.MyDecoderState.READ_LENGTH;

/**
 * https://my.oschina.net/landas/blog/893915
 */
public class IntegerHeaderFrameDecoder extends ReplayingDecoder<MyDecoderState> {
    private int length;

    public IntegerHeaderFrameDecoder() {
        // Set the initial state.
        super(READ_LENGTH);
    }

    /**
     * 为了提高处理复杂消息的性能，反正重复解码，需要缓存已经解码过的内容；比如解析length和content；解析到length后，将数据缓存到变量length中
     * 然后checkpoint，将checkpoint置为当前流中的readerIndex；接着立即解析read_content，解析不到就抛异常REPLAY；
     * REPLAY被父类ReplayingDecoder捕获到；父类是循环读取的，既然抛异常，说明数据不够读，父类可以调出循环了，并且其中已经读取到的部分数据也缓存
     * 起来了，下次不用再进行解码操作了
     *
     * @param ctx the {@link ChannelHandlerContext} which this {@link ByteToMessageDecoder} belongs to
     * @param buf ReplayingDecoderByteBuf 类型的，buffer成员=cumulation
     * @param out the {@link List} to which decoded messages should be added
     * @throws Exception
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) throws Exception {
        switch (state()) {
            case READ_LENGTH:
                length = buf.readInt();
                checkpoint(READ_CONTENT);
            case READ_CONTENT:
                ByteBuf frame = buf.readBytes(length);
                checkpoint(READ_LENGTH);
                out.add(frame);
                break;
            default:
                throw new Error("Shouldn't reach here.");
        }
    }

    enum MyDecoderState {
        READ_LENGTH,
        READ_CONTENT;
    }

}