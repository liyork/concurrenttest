package com.wolf.concurrenttest.program.keyextra;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/27
 */
public class Keyword implements Comparable<Keyword> {

    private String word;
    private int df;//单词对应的文档数

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getDf() {
        return df;
    }

    public void setDf(int df) {
        this.df = df;
    }

    //高到低
    @Override
    public int compareTo(Keyword o) {
        return Integer.compare(o.getDf(), this.getDf());
    }
}
