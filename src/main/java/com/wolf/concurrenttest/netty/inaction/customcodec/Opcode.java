package com.wolf.concurrenttest.netty.inaction.customcodec;

/**
 * Description: 操作码，占1byte
 * <br/> Created on 9/28/17 8:18 PM
 *
 * @author 李超
 * @since 1.0.0
 */
public class Opcode {

    public static final byte GET = 0x00;
    public static final byte SET = 0x01;
    public static final byte DELETE = 0x04;
}
