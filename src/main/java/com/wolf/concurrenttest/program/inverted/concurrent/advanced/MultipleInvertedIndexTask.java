package com.wolf.concurrenttest.program.inverted.concurrent.advanced;

import com.wolf.concurrenttest.program.inverted.concurrent.Document;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/25
 */
public class MultipleInvertedIndexTask implements Runnable {

    private CompletionService<List<Document>> completionService;
    private ConcurrentHashMap<String, ConcurrentLinkedDeque<String>> invertedIndex;

    public MultipleInvertedIndexTask(CompletionService<List<Document>> completionService, ConcurrentHashMap<String, ConcurrentLinkedDeque<String>> invertedIndex) {
        this.completionService = completionService;
        this.invertedIndex = invertedIndex;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    List<Document> documents = completionService.take().get();
                    for (Document document : documents) {
                        updateInvertedIndex(document.geVoc(), invertedIndex, document.getFileName());
                    }
                } catch (InterruptedException e) {
                    break;
                }
            }

            while (true) {
                Future<List<Document>> future = completionService.poll();
                if (null == future) {
                    break;
                }
                List<Document> documents = future.get();
                for (Document document : documents) {
                    updateInvertedIndex(document.geVoc(), invertedIndex, document.getFileName());
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void updateInvertedIndex(Map<String, Integer> voc, ConcurrentHashMap<String, ConcurrentLinkedDeque<String>> invertedIndex, String fileName) {

        for (String word : voc.keySet()) {

            if (word.length() >= 3) {
                invertedIndex.computeIfAbsent(word, k -> new ConcurrentLinkedDeque<>()).add(fileName);
            }
        }
    }
}
