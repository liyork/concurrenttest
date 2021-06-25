package com.wolf.concurrenttest.netty.inaction.customprotocol.memcache;

/**
 * Description: Memcached响应POJO
 * <br/> Created on 9/29/17 8:34 AM
 *
 * @author 李超
 * @since 1.0.0
 */
public class MemcachedResponse {

    private byte magic;
    private byte opCode;
    private byte dataType;// indicate if its binary or text based
    private short status;
    private int id;// unique id
    private long cas;
    private int flags;
    private int expires;// indicate if the value stored for this response will e expire at some point
    private String key;
    private String data;

    public MemcachedResponse(byte magic, byte opCode,
                             byte dataType, short status, int id, long cas,
                             int flags, int expires, String key, String data) {
        this.magic = magic;
        this.opCode = opCode;
        this.dataType = dataType;
        this.status = status;
        this.id = id;

        this.cas = cas;
        this.flags = flags;
        this.expires = expires;
        this.key = key;
        this.data = data;
    }

    public byte magic() {
        return magic;
    }

    public byte opCode() {
        return opCode;
    }

    public byte dataType() {
        return dataType;
    }

    public short status() {
        return status;
    }

    public int id() {
        return id;
    }

    public long cas() {
        return cas;
    }

    public int flags() {
        return flags;
    }

    public int expires() {
        return expires;
    }

    public String key() {
        return key;
    }

    public String data() {
        return data;
    }
}
