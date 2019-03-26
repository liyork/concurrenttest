package com.wolf.concurrenttest.program.inverted.concurrent;

import java.util.Map;
import java.util.concurrent.*;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/25
 */
public class InvertedIndexTask implements Runnable {

    private CompletionService<Document> completionService;
    private ConcurrentHashMap<String, ConcurrentLinkedDeque<String>> invertedIndex;

    public InvertedIndexTask(CompletionService<Document> completionService, ConcurrentHashMap<String, ConcurrentLinkedDeque<String>> invertedIndex) {
        this.completionService = completionService;
        this.invertedIndex = invertedIndex;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    //阻塞获取
                    Document document = completionService.take().get();
                    updateInvertedIndex(document.geVoc(), invertedIndex, document.getFileName());
                } catch (InterruptedException e) {
                    break;//被中断则退出
                }
            }

            //被Interrupte后，再查看一下
            while (true) {
                //非阻塞获取
                Future<Document> future = completionService.poll();
                if (null == future) {
                    break;
                }
                Document document = future.get();
                updateInvertedIndex(document.geVoc(), invertedIndex, document.getFileName());
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
