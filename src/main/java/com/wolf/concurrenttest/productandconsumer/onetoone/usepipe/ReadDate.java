package com.wolf.concurrenttest.productandconsumer.onetoone.usepipe;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PipedReader;

/**
 * Description:
 * <br/> Created on 3/1/18 9:58 AM
 *
 * @author 李超
 * @since 1.0.0
 */
public class ReadDate {

    public void readMethod(PipedInputStream input) throws IOException {
        System.out.println("read :");
        StringBuilder stringBuilder =  new StringBuilder();
        byte[] byteArray = new byte[20];
        int readLength = input.read(byteArray);//若无数据则阻塞
        while (readLength != -1) {
            String newData = new String(byteArray, 0, readLength);
            stringBuilder.append(newData);
            readLength = input.read(byteArray);
        }
        System.out.println(stringBuilder);
        input.close();
    }

    public void readMethod(PipedReader input) throws IOException {
        System.out.println("read :");
        StringBuilder stringBuilder =  new StringBuilder();
        char[] byteArray = new char[20];
        int readLength = input.read(byteArray);//若无数据则阻塞
        while (readLength != -1) {
            String newData = new String(byteArray, 0, readLength);
            stringBuilder.append(newData);
            readLength = input.read(byteArray);
        }
        System.out.println(stringBuilder);
        input.close();
    }
}
