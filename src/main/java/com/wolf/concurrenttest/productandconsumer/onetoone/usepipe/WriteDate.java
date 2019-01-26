package com.wolf.concurrenttest.productandconsumer.onetoone.usepipe;

import java.io.IOException;
import java.io.PipedOutputStream;
import java.io.PipedWriter;

/**
 * Description:
 * <br/> Created on 3/1/18 9:58 AM
 *
 * @author 李超
 * @since 1.0.0
 */
public class WriteDate {

    public void writeMethod(PipedOutputStream out) throws IOException {
        System.out.println("write :");
        StringBuilder stringBuilder =  new StringBuilder();
        for (int i = 0; i < 100; i++) {
            String outDate = "" + (i + 1);
            out.write(outDate.getBytes());
            stringBuilder.append(outDate);
        }
        System.out.println(stringBuilder);
        out.close();
    }

    public void writeMethod(PipedWriter out) throws IOException {
        System.out.println("write :");
        StringBuilder stringBuilder =  new StringBuilder();
        for (int i = 0; i < 100; i++) {
            String outDate = "" + (i + 1);
            out.write(outDate);
            stringBuilder.append(outDate);
        }
        System.out.println(stringBuilder);
        out.close();
    }
}
