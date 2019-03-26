package com.wolf.concurrenttest.program.kmeans.concurrent;

import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Description: 分治思想，对于大量数据，采用多线程分开各自执行各自段。相当于多线程的递归执行
 *
 * @author 李超
 * @date 2019/03/05
 */
public class ConcurrentKMeans {

    public static ConcurrentDocumentCluster[] calculate(ConcurrentDocument[] documents, int numberCluster, int vocSize,
                                                        int seed, int maxSize) {

        ConcurrentDocumentCluster[] clusters = new ConcurrentDocumentCluster[numberCluster];

        Random random = new Random(seed);
        for (int i = 0; i < numberCluster; i++) {
            clusters[i] = new ConcurrentDocumentCluster(vocSize);
            clusters[i].initialize(random);
        }

        boolean change = true;
        ForkJoinPool pool = new ForkJoinPool();

        int numSteps = 0;
        while (change) {
            change = assignment(clusters, documents, maxSize, pool);
            update(clusters, maxSize, pool);
            numSteps++;
        }

        pool.shutdown();
        System.out.println("Number of steps: " + numSteps);
        return clusters;
    }

    private static boolean assignment(ConcurrentDocumentCluster[] clusters, ConcurrentDocument[] documents,
                                      int maxSize, ForkJoinPool pool) {

        for (ConcurrentDocumentCluster cluster : clusters) {
            cluster.clearDocuments();
        }

        AtomicInteger numChanges = new AtomicInteger(0);
        AssignmentTask task = new AssignmentTask(clusters, documents, 0, documents.length, numChanges, maxSize);

        pool.execute(task);
        task.join();//等待，直到所有文档都找到最近的簇，才能执行后续操作

        System.out.println("Number of Changes: " + numChanges);
        return numChanges.get() > 0;
    }

    private static void update(ConcurrentDocumentCluster[] clusters, int maxSize, ForkJoinPool pool) {

        UpdateTask task = new UpdateTask(clusters, 0, clusters.length, maxSize, pool);

        pool.execute(task);
        task.join();//等待
    }
}
