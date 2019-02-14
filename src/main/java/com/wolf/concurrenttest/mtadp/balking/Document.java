package com.wolf.concurrenttest.mtadp.balking;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Description: balking设计：某个任务只能由一个线程处理，晚到的则放弃，这样的线程交互称为balking(犹豫)设计模式。
 * 如文档编辑后，自动保存或者手动保存只能有一个处理。
 * <p>
 * edit和save都需要操作共享资源，防止当文档在保存的过程中如遇到新的内容被编辑时引起的共享资源冲突问题,需要同步。
 *
 * balking模式，如系统资源家在或数据初始化，只能初始一次。
 *
 * @author 李超
 * @date 2019/02/04
 */
public class Document {

    private boolean changed = false;

    private String documentPath;
    private List<String> content = new ArrayList<>();

    private static Thread autoSaveThread;

    public Document(String documentPath) {

        this.documentPath = documentPath;
    }

    public static Document create(String documentPath) {

        Document document = new Document(documentPath);
        autoSaveThread = new Thread(new AutoSaveTask(document), "DocumentAutoSaveThread");
        autoSaveThread.start();

        return document;
    }

    public void edit(String content) {

        synchronized (this) {
            this.content.add(content);
            this.changed = true;
        }
    }

    public void save() {

        synchronized (this) {

            //balking，若已经保存，则返回
            if (!changed) {
                return;
            }

            System.out.println(Thread.currentThread() + " execute the save action");

            System.out.println("write the content:" + content + " to file:" + documentPath + "..");

            this.changed = false;
        }
    }

    public void close() {

        autoSaveThread.interrupt();
    }

    private static class AutoSaveTask implements Runnable {

        private final Document document;

        public AutoSaveTask(Document document) {
            this.document = document;
        }

        @Override
        public void run() {

            while (true) {
                try {
                    document.save();
                    TimeUnit.SECONDS.sleep(4);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }
}
