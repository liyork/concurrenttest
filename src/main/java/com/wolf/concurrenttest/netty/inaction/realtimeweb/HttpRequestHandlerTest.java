package com.wolf.concurrenttest.netty.inaction.realtimeweb;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;
import org.junit.Test;

/**
 * Description: 测试HttpRequestHandler
 * <br/> Created on 9/26/17 8:06 AM
 *
 * @author 李超
 * @since 1.0.0
 */
public class HttpRequestHandlerTest {

    @Test
    public void testHttpRequest() {
        ByteBuf buf = Unpooled.buffer();
        for (int i = 1; i < 10; i++) {
            buf.writeInt(i * -1);
        }
        EmbeddedChannel channel = new EmbeddedChannel(new HttpRequestHandler("/ws/yy"));

        HttpVersion httpVersion = new HttpVersion(HttpVersion.HTTP_1_1.toString(), true);
        HttpMethod httpMethod = new HttpMethod("GET");
        //String uri = "/xx/yy";// index.html的响应
        String uri = "/ws/yy";// HttpRequestHandler之后没有handler了就到tailContext了，就没有响应
        HttpRequest httpRequest = new DefaultFullHttpRequest(httpVersion, httpMethod, uri);
        channel.writeInbound(httpRequest);
        channel.finish();

        Object outbound = channel.readOutbound();
        while (null != outbound) {
            System.out.println("outbound=>" + outbound);
            outbound = channel.readOutbound();
        }
    }
}
