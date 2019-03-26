package com.wolf.concurrenttest.program.searchfile;

import java.io.File;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Description: 依赖并发队列，核数线程执行任务,各自找不同目录，有一个找到则标记found
 *
 * @author 李超
 * @date 2019/02/18
 */
public class ParallelGroupFileTask implements Runnable {

    private final String fileName;
    private final ConcurrentLinkedQueue<File> directories;
    private final Result parallelResult;
    private boolean found;

    public ParallelGroupFileTask(String fileName, ConcurrentLinkedQueue<File> directories, Result parallelResult) {
        this.fileName = fileName;
        this.directories = directories;
        this.parallelResult = parallelResult;
    }

    @Override
    public void run() {

        while (directories.size() > 0) {
            File file = directories.poll();
            try {
                processDirectory(file, fileName, parallelResult);
                if (found) {
                    System.out.printf("%s has found the file%n", Thread.currentThread().getName());
                    System.out.printf("parallel search:path: %s%n", parallelResult.getPath());
                    return;
                }
            } catch (InterruptedException e) {
                System.out.printf("%s has been interrupted%n", Thread.currentThread().getName());
            }
        }
    }

    private void processDirectory(File file, String fileName, Result result) throws InterruptedException {

        File[] contents = file.listFiles();
        if (null == contents || contents.length == 0) {
            return;
        }

        for (File content : contents) {
            if (content.isDirectory()) {
                processDirectory(content, fileName, result);
                if (Thread.currentThread().isInterrupted()) {
                    throw new InterruptedException();
                }
                if (found) {
                    return;
                }
            } else {
                processFile(fileName, result, content);
                if (Thread.currentThread().isInterrupted()) {
                    throw new InterruptedException();
                }
                if (found) {
                    return;
                }
            }
        }
    }

    private void processFile(String fileName, Result result, File content) {

        if (content.getName().equals(fileName)) {
            result.setPath(content.getAbsolutePath());
            found = true;
        }
    }

    public boolean getFound() {
        return found;
    }
}
