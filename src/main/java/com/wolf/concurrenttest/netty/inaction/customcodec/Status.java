package com.wolf.concurrenttest.netty.inaction.customcodec;

/**
 * Description: Possible Memcached operation codes and response statuses
 * 2byte
 * <br/> Created on 9/28/17 8:18 PM
 *
 * @author 李超
 * @since 1.0.0
 */
public class Status {

    public static final short NO_ERROR = 0x0000;
    public static final short KEY_NOT_FOUND = 0x0001;
    public static final short KEY_EXISTS = 0x0002;
    public static final short VALUE_TOO_LARGE = 0x0003;
    public static final short INVALID_ARGUMENTS = 0x0004;
    public static final short ITEM_NOT_STORED = 0x0005;
    public static final short INC_DEC_NON_NUM_VAL = 0x0006;
}
