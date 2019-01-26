package com.wolf.concurrenttest.thread;

import java.io.*;
import java.nio.channels.Channels;

/**
 * <p> Description:
 * 任何实现了InterruptableChanel接口的类的***IO阻塞***(注意这里是阻塞)都是可中断的，中断时抛出ClosedByInterruptedException
 * 实现InterruptableChanel接口的类包括FileChannel,ServerSocketChannel,
 * SocketChannel, Pipe.SinkChannel andPipe.SourceChannel，也就是说，原则上可以实现文件、Socket、管道的可中断IO阻塞操作。
 *
 * 处于大数据IO读写中的线程停止,停止这样的线程的办法是强行close掉io输入输出流对象
 * 最好的建议是将大数据的IO读写操作放在循环中进行，这样可以在每次循环中都有线程停止的时机
 * <p/>
 * Date: 2016/6/23
 * Time: 11:32
 *
 * @author 李超
 * @version 1.0
 * @since 1.0
 */
public class ReadLineRunnable implements Runnable {

    //window中不能被打断
    //	static BufferedReader in = new BufferedReader(
//			new InputStreamReader(System.in));
    //window中能被打断
    static BufferedReader in = new BufferedReader(
    new InputStreamReader(Channels.newInputStream((new FileInputStream(FileDescriptor.in)).getChannel())));

    //前3秒可以正常读写，3秒后主线程中段当前线程
    public void run() {

        String line;
        try {
            //到这里线程会卡住，等待用户输入内容并回车
            while((line = in.readLine()) != null) {
                System.out.println("Read line:'" + line + "'");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
