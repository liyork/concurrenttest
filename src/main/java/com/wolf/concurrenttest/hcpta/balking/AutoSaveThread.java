package com.wolf.concurrenttest.hcpta.balking;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Description: 自动保存线程
 * Created on 2021/9/25 3:31 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class AutoSaveThread extends Thread {
    private final Document document;

    public AutoSaveThread(Document document) {
        super("DocumentAutoSaveThread");
        this.document = document;
    }

    @Override
    public void run() {
        while (true) {
            // 每隔1s自动保存
            try {
                document.save();
                TimeUnit.SECONDS.sleep(1);
            } catch (IOException | InterruptedException e) {
                break;
            }
        }
    }
}
