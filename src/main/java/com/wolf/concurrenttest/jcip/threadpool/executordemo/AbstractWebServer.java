package com.wolf.concurrenttest.jcip.threadpool.executordemo;

import java.net.Socket;

/**
 * Description: 仅为公用
 * Created on 2021/6/30 10:40 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class AbstractWebServer {
    protected void handleRequest(Socket connection) {
        Request req = readRequest(connection);
        if (isShutdownRequest(req)) {
            stop();
        } else {
            dispatchRequest(req);
        }
    }

    private void dispatchRequest(Request req) {
    }

    void stop() {
    }


    private boolean isShutdownRequest(Request req) {
        return false;
    }

    private Request readRequest(Socket connection) {
        return null;
    }

    private class Request {
    }
}
