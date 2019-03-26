package com.wolf.concurrenttest.program.levenshtein.first.concurrent.two;

import com.wolf.concurrenttest.program.levenshtein.first.concurrent.one.BestMatchingBasicTask;
import com.wolf.concurrenttest.program.levenshtein.first.serial.BestMatchingData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Description: 比BestMatchingBasicConcurrentCalculation使用invokeAll
 *
 * @author 李超
 * @date 2019/02/24
 */
public class BestMatchingAdvancedConcurrentCalculation {

    public static BestMatchingData getBestMatchingWords(String word, List<String> dictionary) throws ExecutionException, InterruptedException {

        int numCores = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(numCores);

        int size = dictionary.size();
        int step = size / numCores;

        List<Future<BestMatchingData>> results = new ArrayList<>();

        int startIndex = 0;
        int endIndex = 0;

        List<BestMatchingBasicTask> tasks = new ArrayList<>();

        for (int i = 0; i < numCores; i++) {
            startIndex = i * step;
            if (i == numCores - 1) {
                endIndex = dictionary.size();
            } else {
                endIndex = (i + 1) * step;
            }

            BestMatchingBasicTask task = new BestMatchingBasicTask(startIndex, endIndex, dictionary, word);
            tasks.add(task);
        }
        results = executor.invokeAll(tasks);
        executor.shutdown();

        ArrayList<String> words = new ArrayList<>();
        int minDistance = Integer.MAX_VALUE;
        for (Future<BestMatchingData> future : results) {
            BestMatchingData data = future.get();
            if (data.getDistance() < minDistance) {
                words.clear();
                minDistance = data.getDistance();
                words.addAll(data.getWords());
            } else if (data.getDistance() == minDistance) {
                words.addAll(data.getWords());
            }
        }

        BestMatchingData result = new BestMatchingData();
        result.setDistance(minDistance);
        result.setWords(words);

        return result;
    }
}
