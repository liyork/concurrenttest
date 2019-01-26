package com.wolf.concurrenttest.productandconsumer.onetoone.usepipe;

import java.io.*;

/**
 * Description:
 * 使用Piped进行线程间通信
 * <br/> Created on 3/1/18 10:02 AM
 *
 * @author 李超
 * @since 1.0.0
 */
public class PipedThreadCommunication {

    public static void main(String[] args) throws IOException {
//        testStream();
        testCharacter();
    }

    private static void testStream() throws IOException {
        WriteDate writeDate = new WriteDate();
        ReadDate readDate = new ReadDate();

        PipedInputStream pipedInputStream = new PipedInputStream();
        PipedOutputStream pipedOutputStream = new PipedOutputStream();

        pipedOutputStream.connect(pipedInputStream);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    writeDate.writeMethod(pipedOutputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    readDate.readMethod(pipedInputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private static void testCharacter() throws IOException {
        WriteDate writeDate = new WriteDate();
        ReadDate readDate = new ReadDate();

        PipedReader pipedReader = new PipedReader();
        PipedWriter pipedWriter = new PipedWriter();

        pipedWriter.connect(pipedReader);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    writeDate.writeMethod(pipedWriter);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    readDate.readMethod(pipedReader);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
