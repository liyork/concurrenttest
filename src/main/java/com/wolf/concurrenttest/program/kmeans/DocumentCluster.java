package com.wolf.concurrenttest.program.kmeans;

import java.util.Arrays;
import java.util.Random;

/**
 * Description: 簇
 *
 * @author 李超
 * @date 2019/03/04
 */
public class DocumentCluster {

    private double[] centroid;//向量的质心
    private Document[] documents;//关联文档

    public DocumentCluster(int vocSize) {
    }

    //计算簇的质心使用向量的平均值
    //加总每个文档的word相同index并处以数量得到平均值
    public void calculateCentroid() {

        Arrays.fill(centroid, 0);
        for (Document document : documents) {
            Word vector[] = document.getData();

            for (Word word : vector) {
                centroid[word.getIndex()] += word.getTfIdf();
            }
        }

        for (int i = 0; i < centroid.length; i++) {
            centroid[i] /= documents.length;
        }
    }

    public void initialize(Random random) {

        for (int i = 0; i < centroid.length; i++) {
            centroid[i] = random.nextDouble();
        }
    }

    public void clearClusters() {
    }

    public void addDocument(Document document) {
    }

    public double[] getCentroid() {
        return centroid;
    }

    public static int getDocumentCount(DocumentCluster documentCluster) {
        return documentCluster.documents.length;
    }
}
