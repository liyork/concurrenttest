/**
 *
 */
package com.wolf.concurrenttest.jjzl.bytebuf;

import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;

public class HttpResponse {

    private HttpHeaders header;

    private FullHttpResponse response;

    private byte[] body;

    // 3-right
    public HttpResponse(FullHttpResponse response) {
        this.header = response.headers();
        this.response = response;
        if (response.content() != null) {
            body = new byte[response.content().readableBytes()];
            response.content().getBytes(0, body);
        }
    }

    // 1/2-error
    //public HttpResponse(FullHttpResponse response) {
    //    this.header = response.headers();
    //    this.response = response;
    //}

    public HttpHeaders header() {
        return header;
    }

    // 1-error
    // netty默认的I/O Buffer使用DirectByteBuf，不支持array方法
    //public byte[] body() {
    //    return body = response.content() != null ?
    //            response.content().array() : null;
    //}

    // 2-error
    // 字节拷贝，但是netty的NioEventLoop线程执行完SimpleChannelInboundHandler.channelRead0后，会将内存释放，所以再取时refCnt=0
    //public byte[] body() {
    //    body = new byte[response.content().readableBytes()];
    //    response.content().getBytes(0, body);
    //    return body;
    //}

    // 3-right
    public byte[] body() {
        return body;
    }
}
