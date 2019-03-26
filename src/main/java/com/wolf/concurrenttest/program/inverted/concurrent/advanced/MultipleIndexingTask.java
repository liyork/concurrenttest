package com.wolf.concurrenttest.program.inverted.concurrent.advanced;

import com.wolf.concurrenttest.program.inverted.concurrent.Document;
import com.wolf.concurrenttest.program.inverted.serial.DocumentParser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/25
 */
public class MultipleIndexingTask implements Callable<List<Document>> {

    private List<File> files;

    public MultipleIndexingTask(List<File> files) {
        this.files = files;
    }

    @Override
    public List<Document> call() throws Exception {

        List<Document> documents = new ArrayList<>();

        for (File file : files) {
            DocumentParser parser = new DocumentParser();
            Map<String, Integer> voc = parser.parse(file.getAbsolutePath());
            Document document = new Document();
            document.setFileName(file.getName());
            document.setVoc(voc);
        }

        return documents;
    }
}
