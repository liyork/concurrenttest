package com.wolf.concurrenttest.program.kmeans;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/03/04
 */
public class Document {

    private String fileName;
    private Word[] data;//向量空间模型的表示
    private DocumentCluster cluster;//关联的簇


    public Document(String fileName, int size) {
        this.fileName = fileName;
        this.data = new Word[size];
    }

    public boolean setCluster(DocumentCluster cluster) {
        if (this.cluster == cluster) {
            return false;
        } else {
            this.cluster = cluster;
            return true;
        }
    }

    public Word[] getData() {
        return data;
    }
}
