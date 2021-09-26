package com.wolf.concurrenttest.hcpta.threadpermessage;

import java.io.IOException;

/**
 * Description: 测试程序
 * telnet localhost 13312
 * Created on 2021/9/25 9:36 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ChatTest {
    public static void main(String[] args) throws IOException {
        new ChatServer().startServer();
    }
}
