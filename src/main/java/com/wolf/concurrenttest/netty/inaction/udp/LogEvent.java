package com.wolf.concurrenttest.netty.inaction.udp;

import java.net.InetSocketAddress;

/**
 * Description: 日志事件，将日志转换成POJO并发送
 * Created on 2021/6/23 9:09 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class LogEvent {
    public static final byte SEPARATOR = (byte) ':';
    private final InetSocketAddress source;// get the InetSocketAddress of the source which send the LogEvent
    private final String logfile;// the logEvent was send to
    private final String msg;// actual log message
    private final long received;// timestamp at which the logEvent was received

    public LogEvent(String logfile, String msg) {
        this(null, -1, logfile, msg);
    }

    public LogEvent(InetSocketAddress source, long received, String logfile, String msg) {
        this.source = source;
        this.logfile = logfile;
        this.msg = msg;
        this.received = received;
    }

    public InetSocketAddress getSource() {
        return source;
    }

    public String getLogfile() {
        return logfile;
    }

    public String getMsg() {
        return msg;
    }

    public long getReceived() {
        return received;
    }
}
