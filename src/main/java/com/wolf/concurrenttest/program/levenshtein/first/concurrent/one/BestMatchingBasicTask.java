package com.wolf.concurrenttest.program.levenshtein.first.concurrent.one;

import com.wolf.concurrenttest.program.levenshtein.first.serial.BestMatchingData;
import com.wolf.concurrenttest.program.levenshtein.first.serial.LevenshteinDistance;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/24
 */
public class BestMatchingBasicTask implements Callable<BestMatchingData> {

    private int startIndex;
    private int endIndex;
    private List<String> dictionary;
    private String word;

    public BestMatchingBasicTask(int startIndex, int endIndex, List<String> dictionary, String word) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.dictionary = dictionary;
        this.word = word;
    }

    @Override
    public BestMatchingData call() throws Exception {

        ArrayList<String> results = new ArrayList<>();
        int minDistance = Integer.MAX_VALUE;
        int distance;
        for (int i = startIndex; i < endIndex; i++) {
            distance = LevenshteinDistance.calculate(word, dictionary.get(i));
            if (distance < minDistance) {
                results.clear();//只保留小于等于最小距离的
                minDistance = distance;
                results.add(dictionary.get(i));
            } else if (distance == minDistance) {
                results.add(dictionary.get(i));
            }
        }

        BestMatchingData result = new BestMatchingData();
        result.setWords(results);
        result.setDistance(minDistance);

        return result;
    }
}
