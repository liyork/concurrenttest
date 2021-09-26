package com.wolf.concurrenttest.hcpta.balking;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Description: 文档对象
 * 内部，save和edit同步，防止文档在保存的过程中，若遇到新的内容被编译时引起的共享资源冲突
 * Created on 2021/9/25 3:21 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class Document {
    // 若文档变更则设置为true
    private boolean changed = false;

    // 本次要保存的内容
    private List<String> content = new ArrayList<>();

    private final FileWriter writer;

    // 自动保存文档的线程
    private static AutoSaveThread autoSaveThread;

    public Document(String documentPath, String documentName) throws IOException {
        this.writer = new FileWriter(new File(documentPath, documentName));
    }

    // 静态，创建文档
    public static Document create(String documentPath, String documentName) throws IOException {
        Document document = new Document(documentPath, documentName);
        autoSaveThread = new AutoSaveThread(document);
        autoSaveThread.start();
        return document;
    }

    // 编辑
    public void edit(String content) {
        synchronized (this) {
            this.content.add(content);
            this.changed = true;
        }
    }

    // 关闭，线中断自动保存线程，再关闭writer释放资源
    public void close() throws IOException {
        autoSaveThread.interrupt();
        writer.close();
    }

    // 用于让外部显示进行保存
    public void save() throws IOException {
        synchronized (this) {
            // balking，若文档未被修改(已被保存)，返回
            if (!changed) {
                return;
            }
            System.out.println(Thread.currentThread() + " execute the save action");
            for (String cacheLine : content) {
                this.writer.write(cacheLine);
                this.writer.write("\r\n");
            }
            this.writer.flush();
            this.changed = false;
            this.content.clear();
        }
    }
}
