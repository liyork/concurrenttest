package com.wolf.concurrenttest.program.levenshtein.second.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Description: 将dictionary按照cpu核数切分并放入不同task中异步执行，找到符合条件的直接返回。
 * 将结果集分片然后并行处理。
 *
 * @author 李超
 * @date 2019/02/24
 */
public class ExistBasicConcurrentCalculation {

    public static boolean existWord(String word, List<String> dictionary) throws ExecutionException, InterruptedException {

        int numCores = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(numCores);

        int size = dictionary.size();
        int step = size / numCores;

        List<ExistBasicTask> tasks = new ArrayList<>();
        int startIndex = 0;
        int endIndex = 0;

        for (int i = 0; i < numCores; i++) {
            startIndex = i * step;
            if (i == numCores - 1) {
                endIndex = dictionary.size();
            } else {
                endIndex = (i + 1) * step;
            }

            ExistBasicTask task = new ExistBasicTask(startIndex, endIndex, dictionary, word);
            tasks.add(task);
        }

        try {
            //返回第一个完成执行且没有抛出异常的任务，若所有都抛出异常则抛出ExecutionException
            //内部循环执行所有future，若有get完成则直接返回，否则都执行完抛出用户异常或者ExecutionException
            Boolean result = executor.invokeAny(tasks);
            return result;
        } catch (ExecutionException e) {
            if (e.getCause() instanceof NoSuchElementException) {
                return false;
            } else {
                throw e;
            }
        } finally {
            executor.shutdown();
        }
    }
}
