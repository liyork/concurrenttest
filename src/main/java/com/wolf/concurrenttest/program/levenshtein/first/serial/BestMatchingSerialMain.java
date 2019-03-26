package com.wolf.concurrenttest.program.levenshtein.first.serial;

import java.util.Date;
import java.util.List;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/24
 */
public class BestMatchingSerialMain {

    public static void main(String[] args) {

        String word = "xx123";

        List<String> dictionary = WordsLoaders.load("xxx.txt");

        System.out.println("Dictionary Size: " + dictionary.size());

        Date startTime = new Date();
        BestMatchingData result = BestMatchingSerialCalculation.getBestMatchingWords(word, dictionary);
        List<String> results = result.getWords();

        System.out.println("Word: " + word);
        System.out.println("Minimum distance: " + result.getDistance());
        System.out.println("List of best matching words: " + results.size());
        results.forEach(System.out::println);
        System.out.println("Execution Time: " + (System.currentTimeMillis() - startTime.getTime()));
    }
}
