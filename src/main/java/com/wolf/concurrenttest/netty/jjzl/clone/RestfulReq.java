package com.wolf.concurrenttest.netty.jjzl.clone;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;

import java.util.Arrays;

public class RestfulReq {

    private HttpHeaders header;

    private HttpMethod method;

    private HttpVersion version;

    private byte[] body;

    public RestfulReq(byte[] body) {
        this.body = body;
    }

    // 方法明确返回自身
    public byte[] body() {
        return this.body;
    }

    // 方法明确返回拷贝
    public byte[] bodyCopy() {
        if (this.body != null)
            return Arrays.copyOf(this.body, this.body.length);
        return null;
    }
}
