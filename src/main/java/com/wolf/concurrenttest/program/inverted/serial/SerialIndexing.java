package com.wolf.concurrenttest.program.inverted.serial;

import java.io.File;
import java.util.*;

/**
 * Description: 串行倒排，解析出文件中每行单词，统计单词对应频率，然后统计出单词对应文件
 *
 * @author 李超
 * @date 2019/02/25
 */
public class SerialIndexing {

    public static void main(String[] args) {

        File source = new File("data");
        File[] files = source.listFiles();
        HashMap<String, List<String>> invertedIndex = new HashMap<>();

        Date start = new Date();
        for (File file : files) {
            DocumentParser parser = new DocumentParser();
            if (file.getName().endsWith(".txt")) {
                Map<String, Integer> voc = parser.parse(file.getAbsolutePath());
                updateInvertedIndex(voc, invertedIndex, file.getName());
            }
        }

        System.out.println("Execution Time: " + (System.currentTimeMillis() - start.getTime()));
        System.out.println("invertedIndex: " + invertedIndex.size());
    }

    //单词对应文件名
    private static void updateInvertedIndex(Map<String, Integer> voc, HashMap<String, List<String>> invertedIndex, String fileName) {

        for (String word : voc.keySet()) {
            if (word.length() >= 3) {
                invertedIndex.computeIfAbsent(word, k -> new ArrayList<>()).add(fileName);
            }
        }
    }
}
