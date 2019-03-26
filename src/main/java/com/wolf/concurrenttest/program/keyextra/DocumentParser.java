package com.wolf.concurrenttest.program.keyextra;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/27
 */
public class DocumentParser {

    public static Document parse(String path) {

        Document ret = new Document();
        Path file = Paths.get(path);
        ret.setFileName(file.toString());

        try (BufferedReader reader = Files.newBufferedReader(file)) {

            for (String line : Files.readAllLines(file)) {
                parseLine(line, ret);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ret;
    }

    private static void parseLine(String line, Document ret) {

        //删除重音符号并转换成小写
        line = Normalizer.normalize(line, Normalizer.Form.NFKD);
        line = line.replaceAll("[^\\p{ASCII}]", "");
        line = line.toLowerCase();

        for (String w : line.split("\\W+")) {
            ret.addWord(w);//加总tf
        }
    }
}
