package com.wolf.concurrenttest.program.keyextra;

/**
 * Description: 单词以及频率相关
 *
 * @author 李超
 * @date 2019/02/27
 */
public class Word implements Comparable<Word> {

    private String word;
    private int tf;//术语频次：此单词在一份文档中出现的次数
    private int df;//文档频次：此单词在哪些文档中出现的次数,即含有此单词的文档数目
    //逆文档频次(IDF)：用于度量单词所提供的使用某个文档区别于其他文档的信息，单词很常用则idf值低，
    //但是如果单词在少数几个文档中出现，那么IDF高
    private double tfIdf;

    public Word(String k) {
    }

    /**
     * @param df
     * @param N  集合中文档的数目
     */
    public void setDf(int df, int N) {
        this.df = df;
        tfIdf = tf * Math.log((double) N / df);
    }

    //使用tfIdf排序，高到低
    @Override
    public int compareTo(Word o) {
        return Double.compare(o.getTfIdf(), this.getTfIdf());
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getTf() {
        return tf;
    }

    public void setTf(int tf) {
        this.tf = tf;
    }

    public int getDf() {
        return df;
    }

    public void setDf(int df) {
        this.df = df;
    }

    public double getTfIdf() {
        return tfIdf;
    }

    public void setTfIdf(double tfIdf) {
        this.tfIdf = tfIdf;
    }

    public void addTf() {
        this.tf += getTf() + 1;
    }

    public void addTf(Word word) {
        this.tf += word.getTf();
    }

    private void addDf(Word word) {
        this.df += getDf() + word.getDf();
    }

    public static Word merge(Word word, Word word1) {
        word.addTf(word1);
        word.addDf(word1);

        return word;
    }
}
