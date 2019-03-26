package com.wolf.concurrenttest.program.kmeans.serial;

import com.wolf.concurrenttest.program.kmeans.DistanceMeasurer;
import com.wolf.concurrenttest.program.kmeans.Document;
import com.wolf.concurrenttest.program.kmeans.DocumentCluster;

import java.util.Random;

/**
 * Description: k-means 聚类算法将预先未分类的项集分组到预定的 K 个簇
 * 每一项通常都由一个特征（或属性）向量定义。所有项都有相同数目的属性。
 * 每个簇也由一个含有同样属性数目的向量定义，这些属性描述了所有可分类到该簇的项。该向量叫作 centroid。
 * 例如，如果这些项是用数值型向量定义的，那么簇就定义为划分到该簇的各项的平均值
 * <p>
 * 算法步骤：
 * 初始化：要创建最初代表 K 个簇的向量，通常，你可以随机初始化这些向量。
 * 指派：你可以将每一项划分到一个簇中。为了选择该簇，可以计算项和每个簇之间的距离。
 * 更新：一旦对所有项进行分类之后，必须重新计算定义每个簇的向量。如前所述，通常要计算划分到该簇所有项的向量的平均值。
 * 结束：检查是否有些项改变了为其指派的簇。如果存在变化，需要再次转入指派步骤。否则算法结束，所有项都已分类完毕。
 * <p>
 * 该算法有如下两个主要局限。
 * 如果随机初始化最初的簇向量，那么对同一项集执行两次分类的结果是不同的。
 * 簇的数目是预先定义好的。从分类的视角来看，如果属性选择得不好将会导致糟糕的结果。
 *
 * @author 李超
 * @date 2019/03/04
 */
public class SerialKMeans {

    public static DocumentCluster[] calculate(Document[] documents, int clusterCount, int vocSize, int seed) {

        DocumentCluster[] clusters = new DocumentCluster[clusterCount];

        //初始化
        Random random = new Random(seed);
        for (int i = 0; i < clusterCount; i++) {
            clusters[i] = new DocumentCluster(vocSize);
            clusters[i].initialize(random);
        }

        boolean change = true;
        int numSteps = 0;
        while (change) {
            change = assignment(clusters, documents);
            update(clusters);
            numSteps++;
        }
        System.out.println("Number of steps: " + numSteps);
        return clusters;
    }

    //计算每个文档与各个簇之间的欧式距离，然后指派到距离最短的簇。
    private static boolean assignment(DocumentCluster[] clusters, Document[] documents) {

        for (DocumentCluster cluster : clusters) {
            cluster.clearClusters();
        }

        int numChanges = 0;
        for (Document document : documents) {
            double distance = Double.MAX_VALUE;
            DocumentCluster shortedDistanceCluster = null;
            for (DocumentCluster cluster : clusters) {
                double curDistance = DistanceMeasurer.euclideanDistance(document.getData(), cluster.getCentroid());
                //得到距离最短的簇
                if (curDistance < distance) {
                    distance = curDistance;
                    shortedDistanceCluster = cluster;
                }
            }
            shortedDistanceCluster.addDocument(document);
            boolean result = document.setCluster(shortedDistanceCluster);
            if (result) {
                numChanges++;
            }
        }
        System.out.println("Number of Changes: " + numChanges);
        return numChanges > 0;
    }

    private static void update(DocumentCluster[] clusters) {
        for (DocumentCluster cluster : clusters) {
            cluster.calculateCentroid();
        }
    }
}
