package com.wolf.concurrenttest.underjvm.tooldemo;

import java.io.IOException;
import java.io.InputStream;

/**
 * Description: 测试exec
 * Created on 2021/7/22 10:09 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ExecTest {
    public static void main(String[] args) throws IOException, InterruptedException {
        // Executes the specified string command in a separate process. 会启动一个进程！
        Process exec = Runtime.getRuntime().exec("ps -ef");
        InputStream inputStream = exec.getInputStream();
        byte[] bytes = new byte[1024];
        inputStream.read(bytes);
        System.out.println(new String(bytes));
    }
}
