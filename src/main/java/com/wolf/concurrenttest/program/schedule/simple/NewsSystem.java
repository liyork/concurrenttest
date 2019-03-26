package com.wolf.concurrenttest.program.schedule.simple;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/22
 */
public class NewsSystem implements Runnable {

    private String route;
    private ScheduledThreadPoolExecutor executor;
    private NewsBuffer buffer;
    private CountDownLatch latch = new CountDownLatch(1);

    public NewsSystem(String route) {
        this.route = route;
        executor = new ScheduledThreadPoolExecutor(Runtime.getRuntime().availableProcessors());
        buffer = new NewsBuffer();
    }

    @Override
    public void run() {

        Path file = Paths.get(route);
        NewsWriter newsWriter = new NewsWriter(buffer);
        Thread thread = new Thread(newsWriter, "NewsWriter-thread");
        thread.start();

        try (InputStream in = Files.newInputStream(file);
             BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(";");

                NewsTask task = new NewsTask(data[0], data[1], buffer);
                System.out.println("Task " + task.getName());
                executor.scheduleWithFixedDelay(task, 0, 500, TimeUnit.MILLISECONDS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        synchronized (this) {
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Shutting down the executor.");
        executor.shutdown();
        thread.interrupt();
        System.out.println("The system has finished.");
    }

    public void shutdown() {
        latch.countDown();
    }
}
