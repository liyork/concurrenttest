package com.wolf.concurrenttest.program.levenshtein.second;

import com.wolf.concurrenttest.program.levenshtein.second.concurrent.ExistBasicConcurrentCalculation;
import com.wolf.concurrenttest.program.levenshtein.second.serial.ExistSerialCalculation;
import com.wolf.concurrenttest.program.levenshtein.first.serial.WordsLoaders;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/25
 */
public class ExistMain {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

//        testSerial();
        testConcurrent();
    }

    private static void testSerial() {
        String word = "xx123";

        List<String> dictionary = WordsLoaders.load("xxx.txt");

        System.out.println("Dictionary Size: " + dictionary.size());

        Date startTime = new Date();
        boolean result = ExistSerialCalculation.existWord(word, dictionary);

        System.out.println("Word: " + word);
        System.out.println("Exists: " + result);
        System.out.println("Execution Time: " + (System.currentTimeMillis() - startTime.getTime()));
    }

    private static void testConcurrent() throws ExecutionException, InterruptedException {

        String word = "xx123";

        List<String> dictionary = WordsLoaders.load("xxx.txt");

        System.out.println("Dictionary Size: " + dictionary.size());

        Date startTime = new Date();
        boolean result = ExistBasicConcurrentCalculation.existWord(word, dictionary);

        System.out.println("Word: " + word);
        System.out.println("Exists: " + result);
        System.out.println("Execution Time: " + (System.currentTimeMillis() - startTime.getTime()));
    }
}
