package com.wolf.concurrenttest.program.levenshtein.second.concurrent;

import com.wolf.concurrenttest.program.levenshtein.first.serial.LevenshteinDistance;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.Callable;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/24
 */
public class ExistBasicTask implements Callable<Boolean> {

    private int startIndex;//包括
    private int endIndex;//不包括
    private List<String> dictionary;
    private String word;

    public ExistBasicTask(int startIndex, int endIndex, List<String> dictionary, String word) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.dictionary = dictionary;
        this.word = word;
    }

    @Override
    public Boolean call() throws Exception {

        for (int i = startIndex; i < endIndex; i++) {
            if (LevenshteinDistance.calculate2(word, dictionary.get(i))) {
                return true;
            }
        }

        if (Thread.currentThread().isInterrupted()) {
            return false;
        }

        //适应invokeAny行为,若直接返回false，那么其他task的结果就被忽略了
        throw new NoSuchElementException("The word " + word + " doesn't exists.");
    }
}
