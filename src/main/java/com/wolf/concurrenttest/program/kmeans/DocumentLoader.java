package com.wolf.concurrenttest.program.kmeans;

import com.wolf.concurrenttest.program.kmeans.concurrent.ConcurrentDocument;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/03/04
 */
public class DocumentLoader {

    //每一行都是一个文档
    public static Document[] load(Path path, Map<String, Integer> vocIndex) throws IOException {

        ArrayList<Document> list = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                Document item = processItem(line, vocIndex);
                list.add(item);
            }
        }

        Document[] ret = new Document[list.size()];
        return list.toArray(ret);
    }

    private static Document processItem(String line, Map<String, Integer> vocIndex) {

        String[] tokens = line.split(",");
        int size = tokens.length - 1;

        Document document = new Document(tokens[0], size);
        Word[] data = document.getData();

        for (int i = 1; i < tokens.length; i++) {
            String[] wordInfo = tokens[i].split(":");
            Word word = new Word();
            word.setIndex(vocIndex.get(wordInfo[0]));
            word.setTfIdf(Double.parseDouble(wordInfo[1]));
            data[i - 1] = word;
        }
        Arrays.sort(data);
        return document;
    }

    public static ConcurrentDocument[] load2(Path pathDocs, Map<String, Integer> vocIndex) {
        return null;
    }
}
