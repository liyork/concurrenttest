package com.wolf.concurrenttest.program.serverclient.concurrent;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Description: 异步写日志
 *
 * @author 李超
 * @date 2019/02/20
 */
public class Logger {

    private static ConcurrentLinkedQueue<String> logQueue = new ConcurrentLinkedQueue<>();

    private static Thread thread;
    private static final String LOG_FILE = Paths.get("output", "server.log").toString();

    static {
        LogTask task = new LogTask();
        thread = new Thread(task);
    }

    public static void initializeLog() {
        Path path = Paths.get(LOG_FILE);
        if (Files.exists(path)) {
            try (OutputStream out = Files.newOutputStream(path, StandardOpenOption.TRUNCATE_EXISTING)) {

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        thread.start();
    }

    public static void sendMessage(String message) {
        logQueue.offer(new Date() + ": " + message);
    }

    public static void shutdown() {
        thread.interrupt();
    }

    public static void writeLogs() {
        String message;
        Path path = Paths.get(LOG_FILE);
        try (BufferedWriter fileWriter = Files.newBufferedWriter(path,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {

            while ((message = logQueue.poll()) != null) {//不会阻塞，10s调用此时，有就写入，否则返回。
                fileWriter.write(new Date() + ": " + message);
                fileWriter.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
