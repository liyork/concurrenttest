package com.wolf.concurrenttest.program.kmeans;

import com.wolf.concurrenttest.program.kmeans.concurrent.ConcurrentDocument;
import com.wolf.concurrenttest.program.kmeans.concurrent.ConcurrentDocumentCluster;
import com.wolf.concurrenttest.program.kmeans.concurrent.ConcurrentKMeans;
import com.wolf.concurrenttest.program.kmeans.serial.SerialKMeans;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/03/04
 */
public class KMeansTest {

    public static void main(String[] args) throws IOException {

        testSerial(args);
        testConcurrent(args);
    }

    private static void testSerial(String[] args) throws IOException {
        Path pathVoc = Paths.get("data", "movies.words");
        Map<String, Integer> vocIndex = VocabularyLoader.load(pathVoc);
        System.out.println("Voc Size: " + vocIndex.size());
        Path pathDocs = Paths.get("data", "movies.data");
        Document[] documents = DocumentLoader.load(pathDocs, vocIndex);
        System.out.println("Document Size: " + documents.length);

        if (args.length != 2) {
            System.err.println("Please specify K and SEED");
            return;
        }
        //簇
        int K = Integer.valueOf(args[0]);
        int SEED = Integer.valueOf(args[1]);

        Date start, end;
        start = new Date();
        DocumentCluster[] clusters = SerialKMeans.calculate(documents,
                K, vocIndex.size(), SEED);
        end = new Date();
        System.out.println("K: " + K + "; SEED: " + SEED);
        System.out.println("Execution Time: " + (end.getTime() -
                start.getTime()));
        System.out.println(Arrays.stream(clusters)
                .map(DocumentCluster::getDocumentCount)
                .sorted(Comparator.reverseOrder())
                .map(Object::toString)
                .collect(Collectors.joining(", ", "Cluster sizes: ", "")));
    }

    private static void testConcurrent(String[] args) throws IOException {

        Path pathVoc = Paths.get("data", "movies.words");
        Map<String, Integer> vocIndex = VocabularyLoader.load(pathVoc);
        System.out.println("Voc Size: " + vocIndex.size());
        Path pathDocs = Paths.get("data", "movies.data");
        ConcurrentDocument[] documents = DocumentLoader.load2(pathDocs, vocIndex);
        System.out.println("Document Size: " + documents.length);

        if (args.length != 2) {
            System.err.println("Please specify K and SEED");
            return;
        }
        //簇
        int K = Integer.valueOf(args[0]);
        int SEED = Integer.valueOf(args[1]);

        Date start, end;
        start = new Date();
        ConcurrentDocumentCluster[] clusters = ConcurrentKMeans.calculate(documents,
                K, vocIndex.size(), SEED, 10);
        end = new Date();
        System.out.println("K: " + K + "; SEED: " + SEED);
        System.out.println("Execution Time: " + (end.getTime() -
                start.getTime()));
        System.out.println(Arrays.stream(clusters)
                .map(ConcurrentDocumentCluster::getDocumentCount)
                .sorted(Comparator.reverseOrder())
                .map(Object::toString)
                .collect(Collectors.joining(", ", "Cluster sizes: ", "")));
    }
}
