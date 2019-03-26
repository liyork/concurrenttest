package com.wolf.concurrenttest.program.keyextra.concurrent;

import com.wolf.concurrenttest.program.keyextra.Document;
import com.wolf.concurrenttest.program.keyextra.DocumentParser;
import com.wolf.concurrenttest.program.keyextra.Keyword;
import com.wolf.concurrenttest.program.keyextra.Word;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Phaser;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/27
 */
public class KeywordExtractionTasks implements Runnable {

    private ConcurrentHashMap<String, Word> globalVoc;
    private ConcurrentHashMap<String, AtomicInteger> globalKeywords;

    private ConcurrentLinkedDeque<File> concurrentFileListPhase1;
    private ConcurrentLinkedDeque<File> concurrentFileListPhase2;

    private Phaser phaser;

    private String name;
    private boolean main;

    private int parsedDocuments;
    private int numDocuments;

    public KeywordExtractionTasks(ConcurrentLinkedDeque<File> concurrentFileListPhase1, ConcurrentLinkedDeque<File> concurrentFileListPhase2,
                                  Phaser phaser, ConcurrentHashMap<String, Word> globalVoc, ConcurrentHashMap<String, AtomicInteger> globalKeywords,
                                  boolean main, String name, int numDocuments) {

        this.concurrentFileListPhase1 = concurrentFileListPhase1;
        this.concurrentFileListPhase2 = concurrentFileListPhase2;
        this.globalVoc = globalVoc;
        this.globalKeywords = globalKeywords;
        this.phaser = phaser;
        this.main = main;
        this.name = name;
        this.numDocuments = numDocuments;
    }

    @Override
    public void run() {

        File file;

        //起始等待，一起执行
        phaser.arriveAndAwaitAdvance();

        //解析所有文档，并构建globalVoc
        System.out.println(name + ": Phase 1");
        while ((file = concurrentFileListPhase1.poll()) != null) {
            Document doc = DocumentParser.parse(file.getAbsolutePath());
            for (Word word : doc.getVoc().values()) {
                globalVoc.merge(word.getWord(), word, Word::merge);
            }
            parsedDocuments++;
        }
        System.out.println(name + ": " + parsedDocuments + " parsed.");
        //完成一阶段，等待
        phaser.arriveAndAwaitAdvance();

        //找出每份文档最优tfIdf的10个，放入globalKeywords
        System.out.println(name + ": Phase 2");
        while ((file = concurrentFileListPhase2.poll()) != null) {
            Document doc = DocumentParser.parse(file.getAbsolutePath());
            List<Word> keywords = new ArrayList<>(doc.getVoc().values());
            for (Word word : keywords) {
                Word globalWord = globalVoc.get(word.getWord());
                word.setDf(globalWord.getDf(), numDocuments);
            }
            Collections.sort(keywords);

            if (keywords.size() > 10) {
                keywords = keywords.subList(0, 10);
            }
            for (Word word : keywords) {
                addKeyword(globalKeywords, word.getWord());
            }
        }
        System.out.println(name + ": " + parsedDocuments + " parsed.");

        //主线程执行第三阶段
        if (main) {
            phaser.arriveAndAwaitAdvance();

            Iterator<Map.Entry<String, AtomicInteger>> iterator = globalKeywords.entrySet().iterator();
            Keyword[] orderedGlobalKeywords = new Keyword[globalKeywords.size()];
            int index = 0;
            while (iterator.hasNext()) {
                Map.Entry<String, AtomicInteger> entry = iterator.next();
                Keyword keyword = new Keyword();
                keyword.setWord(entry.getKey());
                keyword.setDf(entry.getValue().get());
                orderedGlobalKeywords[index] = keyword;
                index++;
            }

            System.out.println("Keyword Size: " + orderedGlobalKeywords.length);
            Arrays.parallelSort(orderedGlobalKeywords);
            int counter = 0;
            for (int i = 0; i < orderedGlobalKeywords.length; i++) {
                Keyword keyword = orderedGlobalKeywords[i];
                System.out.println(keyword.getWord() + ": " + keyword.getDf());
                counter++;
                if (counter == 100) {
                    break;
                }
            }
        }

        phaser.arriveAndDeregister();

        System.out.println("Thread " + name + " has finished.");
    }

    private void addKeyword(ConcurrentHashMap<String, AtomicInteger> globalKeywords, String word) {
        globalKeywords.merge(word, new AtomicInteger(1), (o, n) -> {
            o.addAndGet(n.get());
            return o;
        });
    }
}
