package com.wolf.concurrenttest.netty.inaction.spdy;

/**
 * Description:
 * <br/> Created on 9/27/17 8:27 PM
 *
 * @author 李超
 * @since 1.0.0
 */
public class SpdyRequestHandler extends HttpRequestHandler {

    @Override
    protected String getContent() {
        return "This content is transmitted via SPDY\r\n";
    }
}
