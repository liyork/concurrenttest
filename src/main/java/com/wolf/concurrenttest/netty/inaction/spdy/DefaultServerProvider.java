package com.wolf.concurrenttest.netty.inaction.spdy;

import com.wolf.utils.ArrayUtils;
import org.eclipse.jetty.npn.NextProtoNego;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Description:
 * <br/> Created on 9/27/17 7:50 PM
 *
 * @author 李超
 * @since 1.0.0
 */
public class DefaultServerProvider implements NextProtoNego.ServerProvider {
    private static final List<String> PROTOCOLS =
            Collections.unmodifiableList(
                    ArrayUtils.toList(new String[]{"spdy/2", "spdy/3", "http/1.1"}));
    private String protocol;

    @Override
    public void unsupported() {
        protocol = "http/1.1";
    }

    @Override
    public List<String> protocols() {
        return PROTOCOLS;
    }

    @Override
    public void protocolSelected(String protocol) {
        this.protocol = protocol;
    }

    public String getSelectedProtocol() {
        return protocol;
    }
}