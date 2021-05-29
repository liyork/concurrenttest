package com.wolf.concurrenttest.netty.inaction.spdy;

/**
 * Description:SpdyOrHttpChooser会根据不同协议(http/spdy)而自动添加handler到pipeline中
 * <br/> Created on 9/28/17 10:25 AM
 *
 * @author 李超
 * @since 1.0.0
 */
//public class DefaultSpdyOrHttpChooser extends SpdyOrHttpChooser {
//    public DefaultSpdyOrHttpChooser(int maxSpdyContentLength,
//                                    int maxHttpContentLength) {
//        super(maxSpdyContentLength, maxHttpContentLength);
//    }
//
//    @Override
//    protected SelectedProtocol getProtocol(SSLEngine engine) {
//        DefaultServerProvider provider =
//                (DefaultServerProvider) NextProtoNego.get(engine);
//        String protocol = provider.getSelectedProtocol();
//        if (protocol == null) {
//            return SelectedProtocol.UNKNOWN;
//
//        }
//        switch (protocol) {
//            case "spdy/2":
//                return SelectedProtocol.SPDY_2;
//            case "spdy/3":
//                return SelectedProtocol.SPDY_3;
//            case "http/1.1":
//                return SelectedProtocol.HTTP_1_1;
//            default:
//                return SelectedProtocol.UNKNOWN;
//        }
//    }
//
//    @Override
//    protected ChannelInboundHandler createHttpRequestHandlerForHttp() {
//        return new HttpRequestHandler();
//    }
//
//    @Override
//    protected ChannelInboundHandler createHttpRequestHandlerForSpdy() {
//        return new SpdyRequestHandler();
//    }
//}
//需要
//<dependency>
//<groupId>io.netty</groupId>
//<artifactId>netty-all</artifactId>
//<version>4.0.0.Final</version>
//</dependency>