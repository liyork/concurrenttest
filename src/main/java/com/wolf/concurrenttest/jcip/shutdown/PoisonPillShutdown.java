package com.wolf.concurrenttest.jcip.shutdown;

import java.io.File;
import java.io.FileFilter;
import java.util.concurrent.BlockingQueue;

/**
 * Description: shutdown with Poison pill，这里演示单c-单p
 * a recognizable object placed on the queue that means "when you get this, stop".
 * poison pills work only when the number of producers and consumers is known.
 * 本例扩展为多p则需要consumer收到N-p的pills，
 * 本例扩展为多p多c则需要每个producer放入N-c这个pills。
 * poison pills work reliably only with unbounded queues.
 * <p>
 * Created on 2021/7/3 10:38 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class PoisonPillShutdown {
    private static final File POISON = new File("");
    private final IndexerThread consumer = new IndexerThread();
    private final CrawlerThread producer = new CrawlerThread();
    private final BlockingQueue<File> queue;
    private final FileFilter filterFilter;
    private final File root;

    public PoisonPillShutdown(BlockingQueue<File> queue, FileFilter filterFilter, File root) {
        this.queue = queue;
        this.filterFilter = filterFilter;
        this.root = root;
    }

    // producer
    class CrawlerThread extends Thread {
        public void run() {
            try {
                crawl(root);
            } catch (InterruptedException e) {
                // fall through
            } finally {
                while (true) {
                    try {
                        queue.put(POISON);
                    } catch (InterruptedException e) {
                        //retry
                    }
                }
            }
        }

        private void crawl(File root) throws InterruptedException {
        }
    }

    // consumer
    class IndexerThread extends Thread {
        public void run() {
            try {
                while (true) {
                    // 这里若InterruptedException，consumer就不会读到POISON进而可能producer就没法退出
                    // 不过这个是一对一的演示，只有interrupt producer
                    File file = queue.take();
                    if (file == POISON) {
                        break;
                    } else {
                        indexFile(file);
                    }
                }
            } catch (InterruptedException e) {

            }
        }

        private void indexFile(File file) {
        }
    }

    public void start() {
        producer.start();
        consumer.start();
    }

    public void stop() {
        producer.interrupt();
    }

    public void awaitTermination() throws InterruptedException {
        consumer.join();
    }
}
