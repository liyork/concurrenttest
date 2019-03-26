package com.wolf.concurrenttest.program.schedule.simple;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/22
 */
public class NewsWriter implements Runnable {

    private NewsBuffer buffer;

    public NewsWriter(NewsBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public void run() {

        try {
            while (!Thread.currentThread().isInterrupted()) {
//                System.out.println("NewsWriter is running...");
                CommonInformationItem item = buffer.get();
                Path path = Paths.get("/Users/lichao30/tmp/output/" + item.getFileName());

                try (BufferedWriter fileWriter = Files.newBufferedWriter(
                        path, StandardOpenOption.CREATE)) {
                    fileWriter.write(item.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
