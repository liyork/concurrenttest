package com.wolf.concurrenttest.netty.inaction.customprotocol.memcache;


import java.util.Random;

/**
 * Description: Memcached请求，编码顺序要看MemcachedRequestEncoder的Description
 * <br/> Created on 9/28/17 8:10 PM
 *
 * @author 李超
 * @since 1.0.0
 */
public class MemcachedRequest {
    private static final Random rand = new Random();
    private int magic = 5;//0x80;//fixed so hard coded,0x80对应128，如果用byte则会溢出为-1。需要用int类型
    private byte opCode; //the operation e.g. set or get
    private String key; //the key to delete, get or set
    private int flags = 0xdeadbeef; //random
    private int expires; //0表示item never expires
    private String body; //if opCode is set, the value
    private int id = rand.nextInt(); //Opaque, the id of the request, will be echoed back in the response
    private long cas; //data version check...not used
    private boolean hasExtras; //not all ops have extras

    public MemcachedRequest(byte opcode, String key, String value) {
        this.opCode = opcode;
        this.key = key;
        this.body = value == null ? "" : value;
        //only set command has extras in our example
        hasExtras = opcode == Opcode.SET;
    }

    public MemcachedRequest(byte opCode, String key) {
        this(opCode, key, null);
    }

    public int magic() {
        return magic;
    }

    public int opCode() {
        return opCode;
    }

    public String key() {
        return key;
    }

    public int flags() {
        return flags;
    }

    public int expires() {
        return expires;
    }

    public String body() {
        return body;
    }

    public int id() {
        return id;
    }

    public long cas() {
        return cas;
    }

    public boolean hasExtras() {
        return hasExtras;
    }


    public static void main(String[] args) {
        System.out.println(0x80);
    }
}
