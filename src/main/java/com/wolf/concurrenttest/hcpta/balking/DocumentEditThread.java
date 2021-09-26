package com.wolf.concurrenttest.hcpta.balking;

import java.io.IOException;
import java.util.Scanner;

/**
 * Description: 代表主动进行文档编辑的线程
 * Created on 2021/9/25 3:33 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class DocumentEditThread extends Thread {
    private final String documentPath;
    private final String documentName;
    private final Scanner scanner = new Scanner(System.in);

    public DocumentEditThread(String documentPath, String documentName) {
        super("DocumentEditThread");
        this.documentPath = documentPath;
        this.documentName = documentName;
    }

    @Override
    public void run() {
        int times = 0;
        try {
            Document document = Document.create(documentPath, documentName);
            while (true) {
                // 等待键盘输入
                String text = scanner.next();
                if ("quit".equals(text)) {
                    document.close();
                    break;
                }
                document.edit(text);
                // 输入5次后保存文档
                if (times == 5) {
                    document.save();
                    times = 0;
                }
                times++;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
