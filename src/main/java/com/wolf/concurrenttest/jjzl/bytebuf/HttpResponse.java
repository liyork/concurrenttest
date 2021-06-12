/**
 *
 */
package com.wolf.concurrenttest.jjzl.bytebuf;

import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;

// 演示buf的使用时机
public class HttpResponse {

    private HttpHeaders header;

    private FullHttpResponse response;

    private byte[] body;

    // 3-right，构造时直接复制
    public HttpResponse(FullHttpResponse response) {
        this.header = response.headers();
        this.response = response;
        if (response.content() != null) {
            body = new byte[response.content().readableBytes()];
            response.content().getBytes(0, body);
        }
    }

    // error-2/1
    //public HttpResponse(FullHttpResponse response) {
    //    this.header = response.headers();
    //    this.response = response;
    //}

    public HttpHeaders header() {
        return header;
    }

    // error-1
    // netty默认的I/O Buffer使用DirectByteBuf，不支持array方法
    public byte[] body() {
        return body = response.content() != null ?
                response.content().array() : null;
    }

    // error-2/2
    // 字节拷贝，但是netty的NioEventLoop线程执行完SimpleChannelInboundHandler.channelRead0后，
    // 会将内存释放，所以main线程中get后再用body取时refCnt=0，buf已经释放了
    //public byte[] body() {
    //    body = new byte[response.content().readableBytes()];
    //    response.content().getBytes(0, body);
    //    return body;
    //}

    // right-3
    //public byte[] body() {
    //    return body;
    //}
}
