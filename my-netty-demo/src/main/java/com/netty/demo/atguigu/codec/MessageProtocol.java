package com.netty.demo.atguigu.codec;

import lombok.Data;

/**
 * @description:
 * @date:2021/1/27 22:48
 **/
@Data
public class MessageProtocol {

    /**
     * 长度
     */
    private int len;

    /**
     * 数据
     */
    private byte[] content;

}
