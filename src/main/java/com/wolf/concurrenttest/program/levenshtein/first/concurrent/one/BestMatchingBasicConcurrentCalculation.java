package com.wolf.concurrenttest.program.levenshtein.first.concurrent.one;

import com.wolf.concurrenttest.program.levenshtein.first.serial.BestMatchingData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Description: 将dictionary按照cpu核数切分并放入不同task中异步执行，然后再合并。
 * 将结果集分片然后并行处理。
 *
 * @author 李超
 * @date 2019/02/24
 */
public class BestMatchingBasicConcurrentCalculation {

    public static BestMatchingData getBestMatchingWords(String word, List<String> dictionary) throws ExecutionException, InterruptedException {

        int numCores = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(numCores);

        int size = dictionary.size();
        int step = size / numCores;

        ArrayList<Future<BestMatchingData>> results = new ArrayList<>();

        int startIndex = 0;
        int endIndex = 0;

        for (int i = 0; i < numCores; i++) {
            startIndex = i * step;
            if (i == numCores - 1) {
                endIndex = dictionary.size();
            } else {
                endIndex = (i + 1) * step;
            }

            BestMatchingBasicTask task = new BestMatchingBasicTask(startIndex, endIndex, dictionary, word);
            Future<BestMatchingData> future = executor.submit(task);
            results.add(future);
        }

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
