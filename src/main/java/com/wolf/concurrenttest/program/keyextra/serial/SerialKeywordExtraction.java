package com.wolf.concurrenttest.program.keyextra.serial;

import com.wolf.concurrenttest.program.keyextra.Document;
import com.wolf.concurrenttest.program.keyextra.DocumentParser;
import com.wolf.concurrenttest.program.keyextra.Keyword;
import com.wolf.concurrenttest.program.keyextra.Word;

import java.io.File;
import java.util.*;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/27
 */
public class SerialKeywordExtraction {

    public static void main(String[] args) {

        File source = new File("data");
        File[] files = source.listFiles();
        //全局关键字与相关频率，合并后的tf和df
        HashMap<String, Word> globalVoc = new HashMap<>();
        //全局关键字与次数
        HashMap<String, Integer> globalKeywords = new HashMap<>();
        int totalCalls = 0;
        int numDocuments = 0;

        Date start = new Date();

        if (files == null) {
            System.err.println("Unable to read the 'data' folder");
            return;
        }

        //解析所有文档得到每份分档有哪些单词，这些单词的tf值，并且抽取所有单词的tf/df值
        for (File file : files) {
            if (file.getName().endsWith(".txt")) {
                Document document = DocumentParser.parse(file.getAbsolutePath());
                for (Word word : document.getVoc().values()) {
                    globalVoc.merge(word.getWord(), word, Word::merge);
                }
                numDocuments++;
            }
        }
        System.out.println("Corpus: " + numDocuments + " documents.");

        //使用统计后的每个单词的全局的df算出tfIdf,取出前10单词，并统计每个word的在所有文档中出现的次数
        for (File file : files) {
            if (file.getName().endsWith(".txt")) {
                //再次解析，因为文档太多不能第一步时全部放入内存
                Document doc = DocumentParser.parse(file.getAbsolutePath());
                List<Word> keywords = new ArrayList<>(doc.getVoc().values());

                int index = 0;
                for (Word word : keywords) {
                    Word globalWord = globalVoc.get(word.getWord());
                    //计算每个单词在全局的tfIdf，但是后面没有用到呢
                    word.setDf(globalWord.getDf(), numDocuments);
                }

                Collections.sort(keywords);

                if (keywords.size() > 10) {
                    keywords = keywords.subList(0, 10);
                }
                for (Word word : keywords) {
                    addKeyword(globalKeywords, word.getWord());
                    totalCalls++;
                }
            }
        }

        //排序前100keyword
        List<Keyword> orderedGlobalKeywors = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : globalKeywords.entrySet()) {
            Keyword keyword = new Keyword();
            keyword.setWord(entry.getKey());
            keyword.setDf(entry.getValue());
            orderedGlobalKeywors.add(keyword);
        }

        Collections.sort(orderedGlobalKeywors);
        if (orderedGlobalKeywors.size() > 100) {
            orderedGlobalKeywors = orderedGlobalKeywors.subList(0, 100);
        }

        for (Keyword keyword : orderedGlobalKeywors) {
            System.out.println(keyword.getWord() + ": " + keyword.getDf());
        }

        System.out.println("Execution Time: " + (System.currentTimeMillis() - start.getTime()));
        System.out.println("Vocabulary Size: " + globalVoc.size());
        System.out.println("Keyword Size: " + globalKeywords.size());
        System.out.println("Number of Documents: " + numDocuments);
        System.out.println("Total calls: " + totalCalls);
    }

    private static void addKeyword(HashMap<String, Integer> globalKeywords, String word) {

        globalKeywords.merge(word, 1, Integer::sum);
    }
}
