package com.wolf.concurrenttest.program.kmeans;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/03/04
 */
public class Word implements Comparable<Word> {

    private int index;
    private double tfIdf;

    @Override
    public int compareTo(Word o) {
        return Double.compare(o.tfIdf, this.tfIdf);
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public void setTfIdf(double tfIdf) {
        this.tfIdf = tfIdf;
    }

    public int getIndex() {
        return index;
    }

    public double getTfIdf() {
        return tfIdf;
    }
}
