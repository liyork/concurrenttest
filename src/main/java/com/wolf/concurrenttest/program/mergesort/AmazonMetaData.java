package com.wolf.concurrenttest.program.mergesort;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/03/07
 */
public class AmazonMetaData implements Comparable<AmazonMetaData> {

    @Override
    public int compareTo(AmazonMetaData other) {
        return Long.compare(this.getSalesrank(), other.getSalesrank());
    }

    private long getSalesrank() {
        return 0;
    }
}
