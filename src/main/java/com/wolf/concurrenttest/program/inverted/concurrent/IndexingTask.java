package com.wolf.concurrenttest.program.inverted.concurrent;

import com.wolf.concurrenttest.program.inverted.serial.DocumentParser;

import java.io.File;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/25
 */
public class IndexingTask implements Callable<Document> {

    private File file;

    public IndexingTask(File file) {
        this.file = file;
    }

    @Override
    public Document call() throws Exception {

        DocumentParser parser = new DocumentParser();
        Map<String, Integer> voc = parser.parse(file.getAbsolutePath());

        Document document = new Document();
        document.setFileName(file.getName());
        document.setVoc(voc);

        return document;
    }
}
