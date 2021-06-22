package com.wolf.concurrenttest.netty.inaction.spdy;

import org.apache.storm.netty.channel.ChannelUpstreamHandler;
import org.apache.storm.netty.handler.codec.spdy.SpdyOrHttpChooser;
import org.eclipse.jetty.npn.NextProtoNego;

import javax.net.ssl.SSLEngine;

/**
 * Description:SpdyOrHttpChooser会根据不同协议(http/spdy)而自动添加handler到pipeline中
 * <br/> Created on 9/28/17 10:25 AM
 *
 * @author 李超
 * @since 1.0.0
 */
public class DefaultSpdyOrHttpChooser extends SpdyOrHttpChooser {

    public DefaultSpdyOrHttpChooser(int maxSpdyContentLength, int maxHttpContentLength) {
        super(maxSpdyContentLength, maxHttpContentLength);
    }

    @Override
    protected SelectedProtocol getProtocol(SSLEngine engine) {
        DefaultServerProvider provider = (DefaultServerProvider) NextProtoNego.get(engine);
        String protocol = provider.getSelectedProtocol();
        if (protocol == null) {
            return SelectedProtocol.None;
        }
        switch (protocol) {
            //case "spdy/2":
            //    return SelectedProtocol.SPDY_2;
            case "spdy/3":
                return SelectedProtocol.SpdyVersion3;
            case "http/1.1":
                return SelectedProtocol.HttpVersion1_1;
            default:
                return SelectedProtocol.None;
        }
    }

    // todo 下面两个产生了变更
    @Override
    protected ChannelUpstreamHandler createHttpRequestHandlerForHttp() {
        //return new HttpRequestHandler();
        return null;
    }

    @Override
    protected ChannelUpstreamHandler createHttpRequestHandlerForSpdy() {
        //return new SpdyRequestHandler();
        return null;
    }
}
