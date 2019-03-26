package com.wolf.concurrenttest.program.levenshtein.second.serial;

import com.wolf.concurrenttest.program.levenshtein.first.serial.LevenshteinDistance;

import java.util.List;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/25
 */
public class ExistSerialCalculation {

    public static boolean existWord(String word, List<String> dictionary) {

        for (String str : dictionary) {
            if (LevenshteinDistance.calculate2(word, str)) {
                return true;
            }
        }

        return false;
    }
}
