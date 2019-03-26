package com.wolf.concurrenttest.program.keyextra.concurrent;

import com.wolf.concurrenttest.program.keyextra.Word;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
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
public class ConcurrentKeywordExtraction {

    public static void main(String[] args) {

        ConcurrentHashMap<String, Word> globalVoc = new ConcurrentHashMap<>();
        ConcurrentHashMap<String, AtomicInteger> globalKeywords = new ConcurrentHashMap<>();

        Date start = new Date();
        File source = new File("data");
        File[] files = source.listFiles(f -> f.getName().endsWith(".txt"));
        if (files == null) {
            System.err.println("The 'data' folder not found!");
            return;
        }

        ConcurrentLinkedDeque<File> concurrentFileListPhase1 = new ConcurrentLinkedDeque<>(Arrays.asList(files));
        ConcurrentLinkedDeque<File> concurrentFileListPhase2 = new ConcurrentLinkedDeque<>(Arrays.asList(files));

        int numDocuments = files.length;
        int factor = 1;
        int numTasks = factor * Runtime.getRuntime().availableProcessors();
        Phaser phaser = new Phaser();
        Thread[] threads = new Thread[numTasks];
        KeywordExtractionTasks[] tasks = new KeywordExtractionTasks[numTasks];

        for (int i = 0; i < numTasks; i++) {
            tasks[i] = new KeywordExtractionTasks(concurrentFileListPhase1, concurrentFileListPhase2,
                    phaser, globalVoc, globalKeywords, i == 0,
                    "Task" + i, concurrentFileListPhase1.size());
            phaser.register();
            System.out.println(phaser.getRegisteredParties() + " tasks arrived to the Phaser.");
        }

        for (int i = 0; i < numTasks; i++) {
            threads[i] = new Thread(tasks[i]);
            threads[i].start();
        }

        for (int i = 0; i < numTasks; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Is Terminated: " + phaser.isTerminated());
        System.out.println("Execution Time: " + (System.currentTimeMillis() - start.getTime()));
        System.out.println("Vocabulary Size: " + globalVoc.size());
        System.out.println("Number of Documents: " + numDocuments);
    }
}
